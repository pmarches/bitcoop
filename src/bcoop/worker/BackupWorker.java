/**
 * <p>Title: BackupWorker.java</p>
 * <p>Description: A Thread that sends backups to other peers.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import org.apache.log4j.Logger;

import bcoop.backup.BackupPlan;
import bcoop.backup.BackupPlanfactory;
import bcoop.backup.DestinationChooser;
import bcoop.backup.blockfactory.BlockFactory;
import bcoop.backup.blockfactory.BlockIdFactory;
import bcoop.backup.fileselection.FileSelection;
import bcoop.backup.scheduler.JobScheduler;
import bcoop.backup.scheduler.ScheduledJob;
import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.block.NumberedBlock;
import bcoop.block.TransactionBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.exception.BlockRefusedException;
import bcoop.exception.ConnectionRefusedException;
import bcoop.exception.MissingConfigurationException;
import bcoop.exception.NoPeerAvailableException;
import bcoop.exception.StorageLimitException;
import bcoop.exception.StreamClosedException;
import bcoop.identity.Identity;
import bcoop.identity.LocalIdentity;
import bcoop.network.Connection;
import bcoop.network.Network;
import bcoop.network.PeerManager;
import bcoop.network.p2p.CachedNetwork;
import bcoop.repos.BlockRepository;
import bcoop.repos.RepositoryManager;
import bcoop.util.BitCoopFile;
import bcoop.util.Configuration;

/**
 * @author pmarches
 *
 */
public class BackupWorker extends ThreadWorker {
	private BackupPlanfactory planFactory;
	private BlockTracker bTracker;
	private Network network;
	private PeerManager peerManager;
	private JobScheduler jobScheduler;
	
	private LocalIdentity localPeerId;
	private RepositoryManager repoManager;
	private DestinationChooser dChooser;
	private CachedNetwork cNet;
	/*junit*/ long waitTimeBeforeRetrySendBlock=59*1000;
	
	public BackupWorker(LocalIdentity localIdentity, Configuration config, Network network, PeerManager peerManager, BlockTracker bTracker, JobScheduler jobScheduler, RepositoryManager repoManager) throws MissingConfigurationException{
		this.localPeerId = localIdentity;
		this.network = network;
		this.cNet = new CachedNetwork(this.network);
		this.peerManager = peerManager;
		this.bTracker = bTracker;
		this.jobScheduler = jobScheduler;
		this.repoManager = repoManager;
		this.dChooser = new DestinationChooser(peerManager, bTracker, repoManager);
		this.planFactory = new BackupPlanfactory(this.bTracker);
		
		setName("BackupWorker "+ this.localPeerId.getHumanReadableAlias());
	}
	
	synchronized public void execute(){
		try{
			if(!this.peerManager.hasRemotePeers()){
				Logger.getLogger(this.getClass()).info("No peers available, waiting for 1 minute.");
				wait(59*1000);
				return;
			}
			jobScheduler.loadReadyJobs(System.currentTimeMillis());
			if(!jobScheduler.hasJobReady()){
				Logger.getLogger(this.getClass()).info("No jobs ready, waiting for 1 minute.");
				wait(59*1000);
				return;
			}
			
			ScheduledJob job = jobScheduler.getNextScheduledJob();
			Logger.getLogger(this.getClass()).info("Starting Job "+job.toString());
			jobScheduler.removeJob(job);
			executeJob(job);
			Logger.getLogger(this.getClass()).info("Job "+job.toString()+" done");
		}
		catch(IOException e){
			Logger.getLogger(this.getClass()).error("", e);
		}
		catch(NoPeerAvailableException e){
			Logger.getLogger(this.getClass()).info("No peer available, defering job");
		}
		catch(StorageLimitException e){
			Logger.getLogger(this.getClass()).info("Got a storage limit exception. "+e);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void executeJob(ScheduledJob job) throws IOException, NoPeerAvailableException, StorageLimitException{
		FileSelection fSelection = job.getFileSelection();
		Logger.getLogger(this.getClass()).debug("Computing files that match the dataGroup "+job.getDataGroupInformation());
		if(fSelection == null || fSelection.isEmpty()){
			Logger.getLogger(this.getClass()).warn("job "+job.toString()+" did not match any files");
			return;
		}
		Logger.getLogger(this.getClass()).debug("Found "+fSelection.size()+" matching files.");

//		Should we check this here ? This would prevent a dedicated backup machine...
//		long jobSize = fSelection.getTotalSize();
//		if(jobSize > this.repoManager.getGlobalMaximumAllowedSpace()){
//			throw new StorageLimitException("Cannot execute job "+job.getDataGroupInformation()+" since it's size ("+jobSize+") exceeeds the global limit of ("+this.repoManager.getGlobalMaximumAllowedSpace()+")");
//		}
		
		TransactionBlock tBlock = new TransactionBlock(BlockIdFactory.getinstance().getNewBlockId(), job.getDataGroupInformation(), job.getScheduleInformation());
		this.bTracker.addTransaction(tBlock);
		for(BitCoopFile oneFile : fSelection){
			if(oneFile.canRead()==false){
				Logger.getLogger(this.getClass()).warn("Cannot read file "+oneFile.getAbsolutePath());
				continue;
			}
			HeaderBlock hBlock = null;
			BackupPlan plan = planFactory.createBackupPlan(oneFile);
			if(plan.requiresBackup()){
				Logger.getLogger(this.getClass()).debug(oneFile.getAbsolutePath()+" requires backup.");
				hBlock = executePlan(plan);
			}
			else{
				Logger.getLogger(this.getClass()).debug(oneFile.getAbsolutePath()+" is up-to-date");
				//TODO check with remove hosts to ensure they kept the file!
				hBlock = bTracker.getLatestHeaderBlockForFile(oneFile.getAbsolutePath());
			}
			tBlock.addHeaderBlockToTransaction(hBlock);
		}
		tBlock.endTransaction();
		sendBlockToRemote(tBlock);
		Logger.getLogger(this.getClass()).debug("Finished transaction "+Long.toHexString(tBlock.getBlockId()));
		this.cNet.closeAllCachedConnection();
	}
	
	/*junit*/ void sendBlockToRemote(NumberedBlock block){
		final int MAX_NB_TRIES=10;
		Identity previousBlockDestination = null;
		int nbTries = 0;
		while(true){
			if(nbTries >= MAX_NB_TRIES){
				throw new RuntimeException("Could not send the block in "+MAX_NB_TRIES+" tries or less.");
			}
			Identity blockDestination = null;
			try {
				blockDestination = this.dChooser.getDestination(block);
				if(blockDestination.equals(previousBlockDestination)){
					Logger.getLogger(this.getClass()).info("Waiting to retry the same peer again.");
					waitToRetrySendBlock();
					nbTries++;
				}
				previousBlockDestination = blockDestination;
				sendBlockToRemotePeer(block, blockDestination);
				return; //TODO send to more peers?
			} catch (ConnectionRefusedException e) {
				Logger.getLogger(this.getClass()).warn("Could not send block to remote peer "+blockDestination+ " trying some other peer.");
				this.peerManager.removePeer(blockDestination);
			}
			catch(NoPeerAvailableException npae){
				Logger.getLogger(this.getClass()).warn("Sending block to remote failed because no more peers are availabe, waiting for retry.");
				waitToRetrySendBlock();
			} catch (BlockRefusedException e) {
				Logger.getLogger(this.getClass()).warn("Sending block to remote failed because block was refused("+e.getMessage()+"), waiting for retry.");
			} catch (StreamClosedException e) {
				Logger.getLogger(this.getClass()).warn("the stream closed while sending block to remote peer "+blockDestination+ " trying some other peer.");
				this.peerManager.removePeer(blockDestination);
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).warn("IOException occured while sending block to remote peer "+blockDestination+ " trying some other peer.");
				this.peerManager.removePeer(blockDestination);
			}
		}
	}

	private void waitToRetrySendBlock() {
		synchronized(this){
			try {
				wait(this.waitTimeBeforeRetrySendBlock);
			} catch (InterruptedException e) {
				//Ok..
			}
		}
	}

	private void sendBlockToRemotePeer(NumberedBlock block, Identity blockDestination) throws IOException, BlockRefusedException, ConnectionRefusedException, StreamClosedException {
		Connection con = this.cNet.getCachedConnection(blockDestination);
		BlockRepository repositoryforPeer = this.repoManager.getRepositoryForPeer(blockDestination);
		
//		if(!this.repoManager.canStoreBlockSizeInGlobalAllowedSpace(block.getStorageSizeOfBlock())){
//			
//		}
//		if(!this.repoManager.canStoreBlockSizeRemotely(blockDestination, block.getStorageSizeOfBlock())){
//			
//		}
		
		long currentlyAllowedSpace = repositoryforPeer.getAllowedSpace();
		long byteSentToDestination = this.bTracker.getBytesStoredOn(blockDestination);
		if(currentlyAllowedSpace < byteSentToDestination + block.getStorageSizeOfBlock()){
			//Notify the remote peer that we have increased their allowed space..
			long localSpaceForPeer = currentlyAllowedSpace+block.getStorageSizeOfBlock();
			con.offerLocalFreeSpace(localSpaceForPeer);

			//Requires that we allow more space to this peer..
			repositoryforPeer.increaseAllowedSpace(block.getStorageSizeOfBlock());
		}
		
		long bytesOnRemotePeer = con.sendBlock(block);
		this.bTracker.setBytesStoredOnPeer(blockDestination, bytesOnRemotePeer);
		this.bTracker.logBlockWasSentTo(block, blockDestination);
	}
	
	/**
	 * @param plan
	 * @return 
	 * @throws IOException 
	 * @throws NoPeerAvailableException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	private HeaderBlock executePlan(BackupPlan plan) throws IOException, NoPeerAvailableException {
		int blockCounter = 0;
		BlockFactory blockFactory = new BlockFactory(this.localPeerId.getEncryptionKey(), plan.getFilename());
		while(blockFactory.hasNextBlock()){
			try{
				DataBlock newBlock = blockFactory.nextBlock();
				sendBlockToRemote(newBlock);
				blockCounter++;
			}
			catch(IllegalBlockSizeException e){
				Logger.getLogger(this.getClass()).error(e.getLocalizedMessage());
				e.printStackTrace();
			}
			catch(BadPaddingException e){
				Logger.getLogger(this.getClass()).error(e.getLocalizedMessage());
				e.printStackTrace();
			} catch (ShortBufferException e) {
				Logger.getLogger(this.getClass()).error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		HeaderBlock hBlock = blockFactory.getHeaderBlock();
		sendBlockToRemote(hBlock);
		
		Logger.getLogger(this.getClass()).debug("Sent "+blockCounter+" data blocks plus 1 header block to remote");
		bTracker.addHeaderBlock(hBlock);
		return hBlock;
	}
	
	/**
	 * @param job
	 */
	synchronized public void scheduleJob(ScheduledJob job) {
		this.jobScheduler.addJob(job);
		Logger.getLogger(this.getClass()).debug("Added job "+job);
		notifyAll();
	}

	public JobScheduler getJobScheduler() {
		return jobScheduler;
	}
}

/**
 * <p>Title: RestoreWorker.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

import java.io.IOException;
import java.util.Vector;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import bcoop.assembler.BlockAssembler;
import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.identity.Identity;
import bcoop.network.Connection;
import bcoop.network.Network;
import bcoop.restoration.RestoreJob;
import bcoop.util.HashOfArray;

/**
 * @author pmarches
 *
 */
public class RestoreWorker extends ThreadWorker{
    LinkedList<RestoreJob> restoreQueue = new LinkedList<RestoreJob>();
    Network network;
    BlockTracker tracker;
	private SecretKey decryptionKey;
        
	public RestoreWorker(Network network, BlockTracker tracker, SecretKey decryptionKey){
		super("RestoreWorker");
        this.network = network;
        this.tracker = tracker;
        this.decryptionKey = decryptionKey;
	}
	
	synchronized public void execute(){
        //TODO Paralellize the restoration...
        try{
            RestoreJob rJob = restoreQueue.removeFirst();
            Logger.getLogger(this.getClass()).info("Starting restore job "+rJob);
            executeJob(rJob);
            Logger.getLogger(this.getClass()).info("Restore job "+rJob+" done");
        }
        catch(NoSuchElementException e){
        	try {
                wait(20*1000);
            } catch (InterruptedException e1) {
                //OK. Shutdown called...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }                
	}

	protected void executeJob(RestoreJob job) throws IOException {
		for(HeaderBlock hBlock : job.getHeaderBlockList()){
			restoreHeaderToDir(hBlock, job.getRestoreDir());
		}
	}
	
	protected void restoreHeaderToDir(HeaderBlock hBlock, String restoreDirectory) throws IOException{
		BlockAssembler fAssembly = new BlockAssembler(restoreDirectory, hBlock, this.decryptionKey);
		
		HashOfArray<Identity, Long> peersToBlocks = new HashOfArray<Identity, Long>();
		
		LinkedList<Long> dataBlockIds = hBlock.getAssociatedDataBlockId();
		for(Long dBlockId : dataBlockIds){
			Vector<Identity> peersThatHaveBlock = this.tracker.getPeerListForBlock(dBlockId);
			for(Identity peerId : peersThatHaveBlock){
				peersToBlocks.addValue(peerId, dBlockId);
			}
		}
		
		HashSet<Long> blocksNotYetReceived = new HashSet<Long>();
		blocksNotYetReceived.addAll(dataBlockIds);
		
		for(Identity peerId : peersToBlocks.keySet()){
			try {
				Connection con = network.getConnection(peerId);
				Vector<Long> allBlocksOnThisPeer = peersToBlocks.getAllBlockOfPeer(peerId);
				for(long dBlockId : allBlocksOnThisPeer){
					if(blocksNotYetReceived.contains(dBlockId)){
						DataBlock dBlock = (DataBlock) con.requestBlock(dBlockId);
						fAssembly.assembleBlock(dBlock);
						//TODO Handle block not found..
						blocksNotYetReceived.remove(dBlockId);
					}
				}
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    public LinkedList<RestoreJob> getRestoreQueue() {
        return restoreQueue;
    }
    
    synchronized public void shutdown(){
        notifyAll();
        super.shutdown();
    }

	synchronized public void addToRestoreQueue(RestoreJob job) {
        restoreQueue.add(job);
        notifyAll();
	}
}

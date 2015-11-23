/**
 * <p>Title: ClientProtocol.java</p>
 * <p>Description: A protocol to exchange information with a control client. Such as a GUI or a command line interface.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.backup.scheduler.JobScheduler;
import bcoop.backup.scheduler.NowSchedule;
import bcoop.backup.scheduler.Schedule;
import bcoop.backup.scheduler.ScheduledJob;
import bcoop.block.HeaderBlock;
import bcoop.block.TransactionBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.identity.Identity;
import bcoop.network.PeerManager;
import bcoop.repos.RepositoryManager;
import bcoop.restoration.RestoreHeaderJob;
import bcoop.restoration.RestoreJob;
import bcoop.server.BCoopServerBase;
import bcoop.worker.BackupWorker;
import bcoop.worker.RestoreWorker;

/**
 * @author pmarches
 *
 */
public class ClientProtocol {
	public static final int DEFAULT_PORT = 8665;
	
	public enum Operation{
		GET_ALL_FILESET, GET_ALL_SCHEDULE, GET_ALL_JOB,
		RESTORE_FILE, DISCONNECT, GET_ALL_TRANSACTION_BLOCK, GET_BLOCK, BACKUP, RESTORE_TRANSACTION, GET_ALL_PEER
	};
	
	//Error should also include progress report..
	public enum ReturnCode{
		OK, ERROR
	}
	
	public static boolean doExecuteServerSide(BCoopServerBase server, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		ClientProtocol.Operation cmd=null;
		try {
			cmd = (Operation) ois.readObject();
		} catch (ClassNotFoundException e) {
			//Impossible
			e.printStackTrace();
		}
		Logger.getLogger(ClientProtocol.class).debug("Got command :"+cmd);
		switch(cmd){
		case GET_ALL_FILESET:
			s_getDefinedFileset(server.jobScheduler, ois, oos);
			break;
		case GET_ALL_SCHEDULE:
			s_getDefinedSchedule(server.jobScheduler, ois, oos);
			break;
		case GET_ALL_JOB:
			s_getScheduledJobs(server.jobScheduler, ois, oos);
			break;
		case GET_ALL_PEER:
			s_getAllPeer(server.network.getPeerManager(), ois, oos);
			break;
		case RESTORE_FILE:
			s_restoreFile(server.restoreWorker, server.blockTracker, ois, oos);
			break;
		case RESTORE_TRANSACTION:
			s_restoreTransaction(server.restoreWorker, server.blockTracker, ois, oos);
			break;
		case DISCONNECT:
			return false;
		case GET_ALL_TRANSACTION_BLOCK:
			s_getAllTransactionBlock(server.blockTracker, ois, oos);
			break;
		case GET_BLOCK:
			s_getBlock(server.reposManager, ois, oos);
			break;
		case BACKUP:
			s_backup(server.backupWorker, ois, oos);
			break;
			
		default:
			Logger.getLogger(ClientProtocol.class).error("Unkown command received by client Worker :"+cmd);
			throw new IOException("Protocol error");
		}
		return true;
	}
	
	public static void returnOk(ObjectOutputStream oos) throws IOException{
		oos.writeObject(ReturnCode.OK);
		oos.flush();
	}
	public static void returnError(String msg){
		//FIXME! Error number?
	}

	public static Vector<ScheduledJob> c_getScheduledJobs(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		try{
			oos.writeObject(Operation.GET_ALL_JOB);
			oos.flush();
			
			Vector<ScheduledJob> jobs = new Vector<ScheduledJob>(); 
			int nbSchedule = ois.readInt();
			for(int i=0; i<nbSchedule; i++){
				ScheduledJob job = (ScheduledJob) ois.readObject();
				jobs.add(job);
			}
			return jobs;
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
			return null;
		}
		
	}
	public static void s_getScheduledJobs(JobScheduler scheduler, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		Vector<ScheduledJob> jobs = scheduler.getAllJobs();
		oos.writeInt(jobs.size());
		Iterator it = jobs.iterator();
		while(it.hasNext()){
			oos.writeObject(it.next());
		}
		oos.flush();
	}
	
	public static void c_restoreFile(String filename, String destinationDir, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		oos.writeObject(Operation.RESTORE_FILE);
		oos.writeObject(filename);
		oos.writeObject(destinationDir);
		oos.flush();
	}
	
	public static void s_restoreFile(RestoreWorker rWorker, BlockTracker tracker, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		try{
			String filename = (String) ois.readObject();
			String destinationDir = (String) ois.readObject();
			HeaderBlock hBlock = tracker.getLatestHeaderBlockForFile(filename);
			RestoreJob job = new RestoreHeaderJob(hBlock, destinationDir);
			rWorker.addToRestoreQueue(job);
		}
		catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<NamedDataGroup> c_getDefinedFileset(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		try {
			oos.writeObject(Operation.GET_ALL_FILESET);
			oos.flush();
			Vector<NamedDataGroup> fileset = (Vector<NamedDataGroup>) ois.readObject();
			return fileset;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	public static void s_getDefinedFileset(JobScheduler scheduler, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		Vector<NamedDataGroup> fileset = scheduler.getNamedDataGroup();
		oos.writeObject(fileset);
		oos.flush();
	}
	
 	@SuppressWarnings("unchecked")
	public static Vector<Schedule> c_getDefinedSchedule(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		oos.writeObject(Operation.GET_ALL_SCHEDULE);
		oos.flush();
		try {
			return (Vector<Schedule>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}
 	
	private static void s_getDefinedSchedule(JobScheduler jobScheduler, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		Vector<Schedule> allSchedule = jobScheduler.getAllSchedule();
		oos.writeObject(allSchedule);
		oos.flush();
	}

	public static void c_disconnect(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		oos.writeObject(Operation.DISCONNECT);
		oos.flush();
	}

	@SuppressWarnings("unchecked")
	public static Vector<TransactionBlock> c_getAllTransactionBlock(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		try {
			oos.writeObject(Operation.GET_ALL_TRANSACTION_BLOCK);
			oos.flush();
			int nbTransactions = ois.readInt();
			Vector<TransactionBlock> allTransactions = new Vector<TransactionBlock>(nbTransactions);
			for(int i=0; i<nbTransactions; i++){
				allTransactions.add((TransactionBlock) ois.readObject());
			}
			return allTransactions;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}

	private static void s_getAllTransactionBlock(BlockTracker bTracker, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		Vector<TransactionBlock> transactions = bTracker.getAllTransactionBlock();
		oos.writeInt(transactions.size());
		for(TransactionBlock block : transactions){
			oos.writeUnshared(block);
		}
		oos.flush();
	}

	public static TransactionBlock c_getBlock(String peerId, Long transactionBlockId, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		oos.writeObject(Operation.GET_BLOCK);
		oos.writeObject(peerId);
		oos.writeObject(transactionBlockId);
		oos.flush();
		try {
			return (TransactionBlock) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}

	private static void s_getBlock(RepositoryManager reposManager, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		try {
			Identity peerId = (Identity) ois.readObject();
			Long blockId = (Long) ois.readObject();
			oos.writeObject(reposManager.getBlock(peerId, blockId));
			oos.flush();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}

	public static void c_restoreTransaction(String transactionBlockId, String restoreToDirectory, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		oos.writeObject(Operation.RESTORE_TRANSACTION);
		oos.writeObject(transactionBlockId);
		oos.writeObject(restoreToDirectory);		
		oos.flush();
	}

	private static void s_restoreTransaction(RestoreWorker restoreWorker, BlockTracker blockTracker, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		try {
			String transactionId = (String) ois.readObject();
			String restoreToDir = (String) ois.readObject();
	
			RestoreJob job = new RestoreJob(restoreToDir);
			TransactionBlock tBlockToRestore = blockTracker.getTransactionBlock(Long.parseLong(transactionId, 16));
			if(tBlockToRestore==null){
				//TODO Error message.. bad blockId? not discovered?
				return;
			}
			for(HeaderBlock hBlock : tBlockToRestore.getAllHeaderBlock()){
				job.addHeaderBlockToRestore(hBlock);
			}
			restoreWorker.addToRestoreQueue(job);
			
		} catch (ClassNotFoundException e) {
			throw new IOException();
		}
	}



	public static void c_backup(String fileset, Date whenToBackup, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		oos.writeObject(Operation.BACKUP);
		oos.writeObject(fileset);
		oos.writeObject(whenToBackup);
		oos.flush();
	}
	private static void s_backup(BackupWorker backupWorker, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		try{
			String fileset = (String) ois.readObject();
			Date whenToBackup = (Date) ois.readObject();
			
			ScheduledJob job = new ScheduledJob();
			NamedDataGroup dataGroup = backupWorker.getJobScheduler().getNamedDataGroup(fileset);
			job.addNamedDataGroup(dataGroup);
			
			if(whenToBackup == null){
				job.addSchedule(new NowSchedule());
			}
			else{
				throw new NotImplementedException();
/*				Schedule schedule = new Schedule();
				schedule.setDate(whenToBackup);
				job.addSchedule(schedule);
*/			}
			backupWorker.scheduleJob(job);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}

	@SuppressWarnings("unchecked")
	public static Vector<Identity> c_getallPeer(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		oos.writeObject(Operation.GET_ALL_PEER);
		oos.flush();
		try {
			return (Vector<Identity>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}

	private static void s_getAllPeer(PeerManager peerManager, ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
		Identity[] peerArray = peerManager.getAllPeers();
		Vector<Identity> peers = new Vector<Identity>(peerArray.length);
		for(Identity peer : peerArray){
			peers.add(peer);
		}
		oos.writeObject(peers);
		oos.flush();
	}



}

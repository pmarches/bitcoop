/**
 * <p>Title: ClientWorkerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

import java.io.IOException;
import java.util.Vector;
import java.util.LinkedList;

import bcoop.backup.fileselection.DataSelector;
import bcoop.backup.fileselection.IncludeDataSelector;
import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.backup.scheduler.JobScheduler;
import bcoop.backup.scheduler.Schedule;
import bcoop.backup.scheduler.ScheduledJob;
import bcoop.block.TransactionBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.client.api.ClientConnection;
import bcoop.restoration.RestoreJob;
import bcoop.server.BCoopServerBase;
import bcoop.util.BitCoopFile;
import bcoop.util.Configuration;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class ClientWorkerTest extends TestCase {
	private static final String SECRET_PATTERN = ".*\\.secret";
	private static final String SCHEDULE1 = "dataName1";
	private static final String DATANAME1 = "schedule1";

	BCoopServerBase server;
	private ClientWorker cWorker;
	private ClientConnection clientConnection;
	private NamedDataGroup secretFiles;
	private Schedule dailySchedule;
	private TransactionBlock transaction1;
	
	public void setUp(){
		try{
			server = new BCoopServerBase();
			server.jobScheduler = new JobScheduler();
			ScheduledJob simpleJob = new ScheduledJob();
			dailySchedule = new Schedule("daily");
			server.jobScheduler.addSchedule(dailySchedule);
			simpleJob.addSchedule(dailySchedule);
			
			secretFiles = new NamedDataGroup("secret files");
			secretFiles.addSelector(new IncludeDataSelector(SECRET_PATTERN, null));
			server.jobScheduler.addDataGroup(secretFiles);
			simpleJob.addNamedDataGroup(secretFiles);

			server.jobScheduler.addJob(simpleJob);
			
			server.restoreWorker = new RestoreWorker(null, null, null);
			server.blockTracker = new BlockTracker(2, server.network);
			
			transaction1 = new TransactionBlock(1123, DATANAME1, SCHEDULE1);
			server.blockTracker.addTransaction(transaction1);
			
			cWorker = new ClientWorker(new Configuration(), server);
			cWorker.start();
			
			this.clientConnection = new ClientConnection("127.0.0.1");
		}
		catch(IOException e){
			e.printStackTrace();
			fail();
		}
	}
	
	public void tearDown(){
		cWorker.shutdown();
	}
	
	public final void testSchedule() {
		try{
			Vector<ScheduledJob> jobs = this.clientConnection.getAllDefinedJobs();
			assertEquals(1, jobs.size());
			assertEquals("daily : secret files", jobs.get(0).toString());
			DataSelector dSelector = jobs.get(0).getNamedDataGroup(0).getDataSelector(0);
			assertEquals(SECRET_PATTERN, dSelector.getPattern());
		}
		catch(IOException e){
			e.printStackTrace();
			fail();
		}
	}
	
	public final void testRestoreFile() throws IOException {
		BitCoopFile restoreDir = new BitCoopFile("testData/restored");
		restoreDir.mkdirs();
		
		this.clientConnection.restoreFile("some/file.txt", restoreDir.getAbsolutePath());
		
		LinkedList<RestoreJob> jobs = null;
		while(jobs == null || jobs.size() == 0){
			jobs = server.restoreWorker.getRestoreQueue();
			Thread.yield();
		}
		assertEquals(1, jobs.size());
	}
	
	public final void testGet() throws IOException{
		Vector<NamedDataGroup> receivedDataGroup = this.clientConnection.getAllDefinedFileset();
		assertEquals(1, receivedDataGroup.size());
		assertEquals(secretFiles.getName(), receivedDataGroup.get(0).getName());
		assertNotSame(secretFiles, receivedDataGroup.get(0));

		Vector<Schedule> receivedSchedule = this.clientConnection.getAllDefinedSchedule();
		assertEquals(1, receivedSchedule.size());
		assertEquals(dailySchedule.getName(), receivedSchedule.get(0).getName());
		assertNotSame(dailySchedule, receivedSchedule.get(0));

		Vector<TransactionBlock> receivedTransaction = this.clientConnection.getAllTransaction();
		assertEquals(1, receivedTransaction.size());
		assertEquals(transaction1.getDataName(), receivedTransaction.get(0).getDataName());
		assertNotSame(transaction1, receivedTransaction.get(0));
	}
}

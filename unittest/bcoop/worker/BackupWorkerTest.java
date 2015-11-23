/**
 * <p>Title: BackupWorkerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

import java.io.RandomAccessFile;

import mockObject.MockBlockRepository;
import mockObject.MockBlocks;
import mockObject.MockException;
import mockObject.MockIdentityManager;
import mockObject.MockNetworkSetup;

import bcoop.AllTests;
import bcoop.backup.scheduler.JobScheduler;
import bcoop.backup.scheduler.OneFileBootTimeJob;
import bcoop.block.DataBlock;
import bcoop.block.NumberedBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.exception.MissingConfigurationException;
import bcoop.exception.StorageLimitException;
import bcoop.util.BitCoopFile;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class BackupWorkerTest extends TestCase {
    BackupWorker bWorker;
    BlockTracker bTracker;
    MockNetworkSetup setup;
    MockBlockRepository serverRepo;
    MockBlockRepository clientRepo;
    
    public void setUp(){
        try {
	        setup = new MockNetworkSetup();
	        bTracker = new BlockTracker(2, setup.netClient);
	        JobScheduler jobScheduler = new JobScheduler();
			bWorker = new BackupWorker(MockIdentityManager.CLIENT_LOCAL_ID, setup.configClient, setup.netClient, setup.netClient.getPeerManager(), bTracker, jobScheduler, setup.netClient.getRepositoryManager());
	
	        serverRepo = (MockBlockRepository) setup.netServer.getRepositoryManager().getRepositoryForPeer(MockIdentityManager.CLIENT_ID);
	
	        clientRepo = (MockBlockRepository) setup.netClient.getRepositoryManager().getRepositoryForPeer(MockIdentityManager.SERVER_ID);
		} catch (MissingConfigurationException e) {
			e.printStackTrace();
			fail();
		}
    }
    
    public void tearDown(){
        setup.shutdownClientAndServer();
    }

    class Executer extends Thread{
        public void run(){
            try{
                bWorker.execute();
            }
            catch(MockException e){
                //Ok..
            }

        }
    }
    
    public final void testConnectUnreachable(){
    		try {
    			setup.netClient.getConnection(MockIdentityManager.UNREACHABLE_PEER_ID);
				fail();
			} catch (Exception e) {
				//ok
				assertTrue(true);
			}
    }
    
    public final void testExecute() {
        try{
            Executer exec = new Executer();
            exec.start();
            while(exec.getState() == Thread.State.RUNNABLE) Thread.yield();
            synchronized(bWorker){
                bWorker.notifyAll();
            }
            exec.join();

            assertEquals(0, serverRepo.blocks.size());
            assertEquals(0, setup.netClient.getRepositoryManager().getTotalAllowedSpaceForAllPeers());

            BitCoopFile testFile = AllTests.createTestFile(100, null);
            bWorker.scheduleJob(new OneFileBootTimeJob(testFile.getAbsolutePath()));
            bWorker.execute();
            
            serverRepo.waitForBlock(3); //Three blocks should have been sent, Transaction, Header and Data.
            assertEquals(1, serverRepo.tBlocks.size());
            assertEquals(1, serverRepo.hBlocks.size());
            assertEquals(3, serverRepo.blocks.size());
            assertEquals(112, bTracker.getBytesStoredOn(MockIdentityManager.SERVER_ID));
            assertEquals(112, setup.netClient.getRepositoryManager().getTotalAllowedSpaceForAllPeers());
            assertEquals(112, clientRepo.getAllowedSpace());

            //Nothing changed in the file..
            bWorker.scheduleJob(new OneFileBootTimeJob(testFile.getAbsolutePath()));
            bWorker.execute();
            serverRepo.waitForBlock(4);
            assertEquals(2, serverRepo.tBlocks.size()); //A new transaction is created even is the file has not changed
            assertEquals(1, serverRepo.hBlocks.size()); //We still have the old HeaderBlock
            assertEquals(4, serverRepo.blocks.size()); //A new transaction with the old header block has been added
            assertEquals(112, clientRepo.getAllowedSpace());
            assertEquals(112, setup.netClient.getRepositoryManager().getTotalAllowedSpaceForAllPeers());
            
            //Append some data to the test file..
            RandomAccessFile raf = new RandomAccessFile(testFile, "rw");
            raf.seek(raf.length()); //Append at the end.
            raf.write("More data!".getBytes());
            raf.close();
            assertEquals(110, testFile.length());
            
            bWorker.scheduleJob(new OneFileBootTimeJob(testFile.getAbsolutePath()));
            bWorker.execute();
            
            serverRepo.waitForRemovedBlock(1);
            serverRepo.waitForBlock(6); //Three more blocks should have been added. T, H, D, but one T has been removed.
            assertEquals(2, serverRepo.tBlocks.size());
            assertEquals(2, serverRepo.hBlocks.size());
            assertEquals(6, serverRepo.blocks.size());
            assertEquals(1, serverRepo.removedBlocks.size()); //Contains the old header block
            assertEquals(240, clientRepo.getAllowedSpace());
            assertEquals(240, setup.netClient.getRepositoryManager().getTotalAllowedSpaceForAllPeers());
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }
    
    public void testFileTooBigForRepository() throws StorageLimitException{
    	setup.netServer.getRepositoryManager().setGlobalMaximumAllowedSpace(10);
    	serverRepo.setAllowedSpace(10);
    	
    	this.bWorker.waitTimeBeforeRetrySendBlock = 1;
    	NumberedBlock hunderedFifthMegBlock = new DataBlock(0x12345l, 15);
    	try{
    		this.bWorker.sendBlockToRemote(hunderedFifthMegBlock);
    		assertTrue(false);
    	}
    	catch(RuntimeException e){
    		assertTrue(true);
    	}
    }

}

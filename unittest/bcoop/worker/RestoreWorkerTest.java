package bcoop.worker;

import java.io.IOException;

import bcoop.blocktracker.BlockTracker;
import bcoop.exception.StorageLimitException;
import bcoop.restoration.RestoreHeaderJob;
import bcoop.restoration.RestoreJob;
import bcoop.restoration.RestoreTransactionJob;
import bcoop.util.BitCoopFile;
import bcoop.util.OSCapabilities;
import mockObject.MockBlocks;
import mockObject.MockIdentityManager;
import mockObject.MockNetworkSetup;
import junit.framework.TestCase;

public class RestoreWorkerTest extends TestCase {
	private static final String RESTORE_DIR = "testData/restored/";

	MockNetworkSetup setup;
	BlockTracker bTracker;
	
	public void setUp() throws StorageLimitException, IOException{
		setup = new MockNetworkSetup();
		bTracker = new BlockTracker(1, setup.netClient);
		
		setup.mockRepositoryManagerServer.storeBlock(MockIdentityManager.CLIENT_ID, MockBlocks.TRANSACTION_BLOCKS[0]);
		bTracker.logBlockWasSentTo(MockBlocks.TRANSACTION_BLOCKS[0], MockIdentityManager.SERVER_ID);

		setup.mockRepositoryManagerServer.storeBlock(MockIdentityManager.CLIENT_ID, MockBlocks.HEADER_BLOCKS[0]);
		bTracker.logBlockWasSentTo(MockBlocks.HEADER_BLOCKS[0], MockIdentityManager.SERVER_ID);
		setup.mockRepositoryManagerServer.storeBlock(MockIdentityManager.CLIENT_ID, MockBlocks.HEADER_BLOCKS[1]);
		bTracker.logBlockWasSentTo(MockBlocks.HEADER_BLOCKS[1], MockIdentityManager.SERVER_ID);

		setup.mockRepositoryManagerServer.storeBlock(MockIdentityManager.CLIENT_ID, MockBlocks.ENCRYPTED_DATABLOCK[0]);
		bTracker.logBlockWasSentTo(MockBlocks.ENCRYPTED_DATABLOCK[0], MockIdentityManager.SERVER_ID);
		setup.mockRepositoryManagerServer.storeBlock(MockIdentityManager.CLIENT_ID, MockBlocks.ENCRYPTED_DATABLOCK[1]);
		bTracker.logBlockWasSentTo(MockBlocks.ENCRYPTED_DATABLOCK[1], MockIdentityManager.SERVER_ID);
		setup.mockRepositoryManagerServer.storeBlock(MockIdentityManager.CLIENT_ID, MockBlocks.ENCRYPTED_DATABLOCK[2]);
		bTracker.logBlockWasSentTo(MockBlocks.ENCRYPTED_DATABLOCK[2], MockIdentityManager.SERVER_ID);
	}
	
	public void tearDown(){
		setup.shutdownClientAndServer();
	}

	/*
	 * Test method for 'bcoop.worker.RestoreWorker.execute()'
	 */
	public void testRestoreHeaderBlock() {
		RestoreWorker rWorker = new RestoreWorker(setup.netClient, bTracker, MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey());
		RestoreJob dBlock1Job = new RestoreHeaderJob(MockBlocks.HEADER_BLOCKS[0], RESTORE_DIR);
		try {
			rWorker.executeJob(dBlock1Job);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		BitCoopFile restoredFile = new BitCoopFile(RESTORE_DIR+MockBlocks.HEADER_BLOCKS[0].getFilename());
		assertTrue(restoredFile.exists());
		assertEquals(MockBlocks.CLEAR_DATABLOCK[0].getBlockData().length+MockBlocks.CLEAR_DATABLOCK[1].getBlockData().length, restoredFile.length());
		
		restoredFile.delete();

		RestoreJob job2 = new RestoreHeaderJob(MockBlocks.HEADER_BLOCKS[2], RESTORE_DIR);
		try {
			rWorker.executeJob(job2);
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
		restoredFile = new BitCoopFile(RESTORE_DIR+MockBlocks.HEADER_BLOCKS[2].getFilename());
		assertTrue(restoredFile.exists());
		assertTrue(restoredFile.isDirectory());
		
		restoredFile.delete();

		RestoreJob job3 = new RestoreHeaderJob(MockBlocks.HEADER_BLOCKS[3], RESTORE_DIR);
		try {
			rWorker.executeJob(job3);
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
		restoredFile = new BitCoopFile(RESTORE_DIR+MockBlocks.HEADER_BLOCKS[3].getFilename());
		if(OSCapabilities.isLinkSupported()==false){
			assertFalse(restoredFile.exists());
		}
		else{
			assertTrue(restoredFile.exists());
			assertTrue(restoredFile.isLink());
			assertEquals(MockBlocks.LINK_DESTINATION, restoredFile.getLinkDestination());
			
			restoredFile.delete();
		}
	}
	
	public void testRestoreTransaction(){
		RestoreWorker rWorker = new RestoreWorker(setup.netClient, bTracker, MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey());
		RestoreJob tBlock1Job = new RestoreTransactionJob(MockBlocks.TRANSACTION_BLOCKS[0], RESTORE_DIR);
		try {
			rWorker.executeJob(tBlock1Job);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		BitCoopFile restoredFile1 = new BitCoopFile(RESTORE_DIR+MockBlocks.HEADER_BLOCKS[0].getFilename());
		assertTrue(restoredFile1.exists());
		assertEquals(MockBlocks.CLEAR_DATABLOCK[0].getBlockData().length+MockBlocks.CLEAR_DATABLOCK[1].getBlockData().length, restoredFile1.length());
		restoredFile1.delete();
		
		BitCoopFile restoredFile2 = new BitCoopFile(RESTORE_DIR+MockBlocks.HEADER_BLOCKS[1].getFilename());
		assertTrue(restoredFile2.exists());
		assertEquals(MockBlocks.CLEAR_DATABLOCK[2].getBlockData().length, restoredFile2.length());
		restoredFile2.delete();
	}

}

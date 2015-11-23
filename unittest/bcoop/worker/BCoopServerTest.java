/**
 * <p>Title: BCoopServerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

import java.util.LinkedList;

import mockObject.MockBlockRepository;
import mockObject.MockIdentityManager;
import mockObject.MockNetworkSetup;

import bcoop.AllTests;
import bcoop.backup.fileselection.ExcludeDataSelector;
import bcoop.backup.fileselection.FileSelection;
import bcoop.backup.fileselection.IncludeDataSelector;
import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.backup.scheduler.NowSchedule;
import bcoop.backup.scheduler.OneFileBootTimeJob;
import bcoop.backup.scheduler.ScheduledJob;
import bcoop.block.HeaderBlock;
import bcoop.server.BCoopServer;
import bcoop.util.BitCoopFile;
import bcoop.util.Configuration;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class BCoopServerTest extends TestCase {
	BCoopServer bcoopClient, bcoopServer;
	MockNetworkSetup setup;
	
	public void setUp(){
		setup = new MockNetworkSetup(); 
		setup.configServer.setProperty(Configuration.BASE_DIR, "testData/peer1");
		setup.configClient.setProperty(Configuration.BASE_DIR, "testData/peer2");
		setup.configClient.setProperty(Configuration.CLIENT_PORT, "6548");
		
		try{
			bcoopServer = new BCoopServer(MockIdentityManager.mockIdentityManagerServer, setup.configServer, setup.netServer);
			bcoopClient = new BCoopServer(MockIdentityManager.mockIdentityManagerClient, setup.configClient, setup.netClient);
			
			bcoopServer.start();
			bcoopClient.start();
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}		
	}
	
	public void tearDown(){
		bcoopClient.shutdown();
		bcoopServer.shutdown();
		setup.shutdownClientAndServer();
	}
	
	public final void testSendOneFile() {
		try{
			BitCoopFile tempFile = AllTests.createTestFile(0, "Allo mon grow".getBytes());
			bcoopClient.scheduleJob(new OneFileBootTimeJob(tempFile.getAbsolutePath()));
			
			MockBlockRepository repoServer = (MockBlockRepository) setup.mockRepositoryManagerServer.getRepositoryForPeer(MockIdentityManager.CLIENT_ID);
			repoServer.waitForBlock(3);
			
			assertEquals(3, repoServer.blocks.size());
			
			HeaderBlock hBlock = null;
			while(hBlock == null){
				hBlock = bcoopClient.getBlockTracker().getLatestHeaderBlockForFile(tempFile.getAbsolutePath());
				Thread.sleep(100);
			}
			assertNotNull(hBlock);
			LinkedList<Long> childBlocks = hBlock.getAssociatedDataBlockId();
			assertEquals(1, childBlocks.size());
			assertEquals(childBlocks.getFirst().longValue(), repoServer.getBlock(childBlocks.getFirst().longValue()).getBlockId());
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	public void testSendManyFiles(){
		BitCoopFile filestoBackup = new BitCoopFile("testData/filesToBackup");
		String curDir = filestoBackup.getAbsolutePath();
		
		ScheduledJob job2 = new ScheduledJob();
		job2.addSchedule(new NowSchedule());
		NamedDataGroup importantStuff = new NamedDataGroup("importantStuff");
		importantStuff.addSelector(new IncludeDataSelector(curDir+"/importantStuff/.*", null));
		importantStuff.addSelector(new ExcludeDataSelector(".*/.svn/.*", null));
		job2.addNamedDataGroup(importantStuff);
		
		NamedDataGroup importantStaticStuff = new NamedDataGroup("importantStaticStuff");
		importantStaticStuff.addSelector(new IncludeDataSelector(curDir+"/oldStaticImportantStuff/.*", null));
		importantStaticStuff.addSelector(new ExcludeDataSelector(".*/.svn/.*", null));
		job2.addNamedDataGroup(importantStaticStuff);
		FileSelection selection = job2.getFileSelection(); 
		assertEquals(selection.toString(), 6, selection.size());
		bcoopClient.scheduleJob(job2);
		
		final int NB_BLOCK_EXPECTED = 13;
		MockBlockRepository repoServer = (MockBlockRepository) setup.mockRepositoryManagerServer.getRepositoryForPeer(MockIdentityManager.CLIENT_ID);
		repoServer.waitForBlock(NB_BLOCK_EXPECTED);
		
		assertEquals(NB_BLOCK_EXPECTED, repoServer.blocks.size());
	}
}

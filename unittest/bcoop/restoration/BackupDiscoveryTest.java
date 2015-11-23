package bcoop.restoration;

import java.io.IOException;

import bcoop.block.HeaderBlock;
import bcoop.exception.StorageLimitException;
import bcoop.identity.Identity;
import bcoop.util.HashMatrix;
import mockObject.MockIdentityManager;
import mockObject.MockNetworkSetup;
import junit.framework.TestCase;

public class BackupDiscoveryTest extends TestCase {
	private String FILENAME = "filename.txt";
	private HeaderBlock hBlock = new HeaderBlock(123, FILENAME, 123);
		
	public void testBackupDiscovery() throws StorageLimitException {
		try{
			MockNetworkSetup netSetup = new MockNetworkSetup();
			netSetup.netServer.getPeerManager().waitForPeer(1);
			netSetup.netClient.getPeerManager().waitForPeer(1);

			netSetup.netServer.getRepositoryManager().storeBlock(MockIdentityManager.CLIENT_ID, hBlock);
			
			BackupDiscovery bDiscovery = new BackupDiscovery(netSetup.netClient.getPeerManager(), netSetup.netClient);
			HashMatrix<String, Identity, HeaderBlock> peerAndFiles = bDiscovery.discoverHeaderBlocks();
			assertNotNull(peerAndFiles.get(FILENAME, MockIdentityManager.SERVER_ID));
			bDiscovery.addMatchFilter(".*dat");
			
			peerAndFiles = bDiscovery.discoverHeaderBlocks();
			assertNull(peerAndFiles.get("Filename that does not exist", MockIdentityManager.SERVER_LOCAL_ID));

			netSetup.shutdownClientAndServer();
		}
		catch(IOException e){
			e.printStackTrace();
			fail();
		}
	}
}

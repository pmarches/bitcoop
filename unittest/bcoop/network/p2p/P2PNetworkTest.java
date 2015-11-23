/**
 * <p>Title: P2PNetworkTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p;

import java.util.Arrays;

import mockObject.MockBlockRepository;
import mockObject.MockIdentityManager;
import mockObject.MockNetworkSetup;

import bcoop.block.DataBlock;
import bcoop.identity.Identity;
import bcoop.network.Connection;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class P2PNetworkTest extends TestCase {
	DataBlock block;
	MockNetworkSetup setup;
	
	public void setUp(){
		block = new DataBlock(123, "Allo lalallilalere..".getBytes());
        setup = new MockNetworkSetup();
        
        setup.waitForPeers();
	}

	public void tearDown(){
		setup.shutdownClientAndServer();
	}

	public final void testP2PNetwork() {
		try{
			assertEquals(2, setup.netClient.getPeerManager().countRemoteAdvertisement());
			assertEquals(1, setup.netServer.getPeerManager().countRemoteAdvertisement());
			Connection conToServer = setup.netClient.getConnection(MockIdentityManager.SERVER_ID);
			assertNotNull(conToServer);
			conToServer.sendBlock(block);

			MockBlockRepository repo = (MockBlockRepository) setup.mockRepositoryManagerServer.getRepositoryForPeer(MockIdentityManager.CLIENT_ID);
			repo.waitForBlock(1);
			
			Identity randomPeerId1 = setup.netClient.getPeerManager().getRandomPeerAdvertisement().peerId;
			assertTrue(MockIdentityManager.SERVER_ID.equals(randomPeerId1) || MockIdentityManager.UNREACHABLE_PEER_ID.equals(randomPeerId1));
			
			setup.netClient.getPeerManager().waitForPeer(2);
			assertEquals(2, setup.netClient.getPeerManager().countRemoteAdvertisement());
			Identity randomPeerId2;
			while(true){ //Test the fact that we can get all the different peers from a random call.
				randomPeerId2 = setup.netServer.getPeerManager().getRandomPeerAdvertisement().peerId;
				if(randomPeerId1.equals(randomPeerId2) == false){
					break;
				}
			}
			assertTrue(MockIdentityManager.CLIENT_ID.equals(randomPeerId2) || MockIdentityManager.UNREACHABLE_PEER_ID.equals(randomPeerId2));
			
			DataBlock retrievedBlock = (DataBlock) setup.mockRepositoryManagerServer.getBlock(MockIdentityManager.CLIENT_ID, block.getBlockId());
			assertEquals(block.getBlockId(), retrievedBlock.getBlockId());
			assertTrue(Arrays.equals(block.getBlockData(), retrievedBlock.getBlockData()));
		}
		catch(Exception e){
			e.printStackTrace();
            fail();
		}
	}

}


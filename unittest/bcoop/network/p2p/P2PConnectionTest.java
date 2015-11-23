/**
 * <p>Title: P2PConnectionTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p;

import java.io.IOException;
import java.util.Vector;
import java.util.Arrays;

import mockObject.MockBlockReceiverHandler;
import mockObject.MockIdentityManager;
import mockObject.MockRepositoryManager;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.exception.BlockRefusedException;
import bcoop.exception.StorageLimitException;
import bcoop.exception.StreamClosedException;
import bcoop.network.Challenge;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.network.p2p.advertisement.PeerAdvertisement;
import bcoop.util.Configuration;
import bcoop.util.MessageDigest;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class P2PConnectionTest extends TestCase {
	DataBlock dBlock = new DataBlock(456934, "BlockData".getBytes());
	HeaderBlock hBlock = new HeaderBlock(0x345a11, "filename", 0);
	private P2PNetwork serverNetwork;
	MockBlockReceiverHandler mockBlockReceiver;
	P2PConnection connectionToServer;
	MockRepositoryManager repoManager;
	
	public void setUp(){
		try{
			Configuration config = new Configuration();
			String serverIp = "127.0.0.1";
			String serverPort = "34567";
			config.setProperty(Configuration.OUR_SERVER_PORT, serverPort);
			
			serverNetwork = new P2PNetwork(MockIdentityManager.SERVER_LOCAL_ID, config);
			mockBlockReceiver = new MockBlockReceiverHandler(); 
			serverNetwork.setBlockReceiverHandler(mockBlockReceiver);
			
			repoManager = new MockRepositoryManager();
			serverNetwork.setRepositoryManager(repoManager);
			
			serverNetwork.bootNetwork();
			
			connectionToServer = new P2PConnection(MockIdentityManager.CLIENT_LOCAL_ID, serverIp, Integer.parseInt(serverPort));
			
			hBlock.registerDataBlock(dBlock, 0, 0);
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	public void tearDown(){
		serverNetwork.shutdownNetwork();
	}
	
	public final void testSendBlock() throws BlockRefusedException, IOException {
		connectionToServer.sendBlock(dBlock);
		mockBlockReceiver.waitForBlock(1);
		assertEquals(1, mockBlockReceiver.nbCall);
	}
	
	public final void testExchangePeer() {
		try{
			PeerAdvertisement localPeerAd = new PeerAdvertisement(MockIdentityManager.PEER_ID1);
			AdvertisementManager adMan = new AdvertisementManager(localPeerAd);
			
			connectionToServer.exchangePeer(adMan);
			assertEquals(1, adMan.countRemoteAdvertisement());
			assertEquals(MockIdentityManager.SERVER_ID, adMan.getRandomPeerAdvertisement().peerId);
		}
		catch (StreamClosedException e) {
			//Stream closed in a dirty manner..
		}
		catch(IOException e){
			e.printStackTrace();
			fail();
		}
	}
	
	public final void testChallenge() throws StorageLimitException {
		try{
			repoManager.storeBlock(MockIdentityManager.CLIENT_ID, dBlock);
			int salt = 98768736;
			
			Challenge challenge = new Challenge(dBlock.getBlockId(), salt);
			challenge.expectedHash = MessageDigest.computeHash(dBlock, salt);
			
			connectionToServer.challenge(challenge);
			assertTrue(Arrays.equals(challenge.expectedHash, challenge.hash));
		}
		catch(IOException e){
			e.printStackTrace();
			fail();
		}
	}
	
	public final void testRequestBlock() throws StorageLimitException {
		try{
			repoManager.storeBlock(MockIdentityManager.CLIENT_ID, dBlock);
			
			DataBlock receivedBlock = (DataBlock) connectionToServer.requestBlock(dBlock.getBlockId());
			assertEquals(dBlock.getBlockId(), receivedBlock.getBlockId());
			assertTrue(Arrays.equals(dBlock.getBlockData(), receivedBlock.getBlockData()));
		}
		catch(IOException e){
			e.printStackTrace();
			fail();
		}
	}
	
	public final void testClose() {
		try{
			connectionToServer.close();
			connectionToServer.sendBlock(null);
			fail();
		}
		catch(Exception e){
			assertTrue(true);
		}
	}
	
	public void testRequestAllHeaderBlock() throws StorageLimitException{
		try {
			repoManager.storeBlock(MockIdentityManager.CLIENT_ID, hBlock);
			Vector<HeaderBlock> headerBlocks = connectionToServer.requestAllHeaderBlock();
			assertEquals(1, headerBlocks.size());
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}

package bcoop.bootfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import mockObject.MockIdentityManager;

import bcoop.network.p2p.advertisement.PeerAdvertisement;
import junit.framework.TestCase;

public class BootFileTest extends TestCase {
	/*
	 * Test method for 'bcoop.bootfile.BootFile.saveToStream(OutputStream)'
	 */
	public void testSaveToStream() throws IOException {
		PeerAdvertisement PEER1 = new PeerAdvertisement();
		PEER1.peerId = MockIdentityManager.PEER_ID1;
		PEER1.setIpAddress("123.456.789.123");
		PEER1.setPort(8080);
		PeerAdvertisement PEER2 = new PeerAdvertisement();
		PEER2.peerId = MockIdentityManager.PEER_ID2;
		PEER2.setIpAddress("321.543.634.432");
		PEER2.setPort(8080);
		
		BootFile bFile = new BootFile();
		bFile.setLocalIdentity(MockIdentityManager.CLIENT_LOCAL_ID);
		bFile.addPeer(PEER1);
		bFile.addPeer(PEER2);
		
		ByteArrayOutputStream baos  = new ByteArrayOutputStream();
		bFile.saveToStream(baos);
		String baosString = baos.toString();
		assertTrue(baosString.length()+" "+baosString, baosString.length() > 1000); //It seems that the windows and Mac implementation generate PEM keys of different lengths

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		BootFile bFileRead = new BootFile();
		bFileRead.readFromStream(bais);
		bais.close();
		
		assertEquals(bFile.getLocalIdentity(), bFileRead.getLocalIdentity());
		assertTrue(Arrays.deepEquals(bFile.getAllPeers(), bFileRead.getAllPeers()));
	}

}

package bcoop.network.p2p.advertisement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mockObject.MockIdentityManager;

import junit.framework.TestCase;

public class AdvertisementManagerArchiverTest extends TestCase {

	public void testLoadAndSaveTo() {
		PeerAdvertisement ourAd = new PeerAdvertisement(MockIdentityManager.PEER_ID1);
		PeerAdvertisement savablePeer = new PeerAdvertisement(MockIdentityManager.PEER_ID3);
		savablePeer.setIpAddress("192.168.0.1");
		savablePeer.setPort(123);
		savablePeer.setExpirationTime(Long.MAX_VALUE);
		
		PeerAdvertisement unsavablePeer = new PeerAdvertisement(MockIdentityManager.PEER_ID2);
		unsavablePeer.setIpAddress("192.168.0.2");
		unsavablePeer.setPort(3456);

		AdvertisementManager srcAdManager = new AdvertisementManager(ourAd);
		srcAdManager.addPeer(savablePeer);
		srcAdManager.addPeer(unsavablePeer);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		AdvertisementManagerArchiver.saveTo(srcAdManager, os);
		
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		try {
			AdvertisementManager generatedAdMan = AdvertisementManagerArchiver.loadFrom(is);
			assertEquals(2, generatedAdMan.countRemoteAdvertisement());
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
	}

}

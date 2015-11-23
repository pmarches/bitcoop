/**
 * <p>Title: AdvertisementManagerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.advertisement;

import java.security.PublicKey;

import bcoop.identity.Identity;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class AdvertisementManagerTest extends TestCase {

	private PeerAdvertisement[] newPeers;
	private AdvertisementManager adManager;
	PeerAdvertisement ourPeerAd, peer2Ad, peer3Ad, tooFarAwayAd;
    
	public void setUp(){
        ourPeerAd = AdvertisementManager.createPeerAdvertisement(new Identity(0x1, "ourPeer", (PublicKey) null), 12345, AdvertisementManager.EXPIRATION_DELTA);
		adManager = new AdvertisementManager(ourPeerAd);

        peer2Ad = AdvertisementManager.createPeerAdvertisement(new Identity(0x2, "peer2", (PublicKey) null), 0, AdvertisementManager.EXPIRATION_DELTA);
        peer3Ad = AdvertisementManager.createPeerAdvertisement(new Identity(0x3, "peer3", (PublicKey) null), 0, AdvertisementManager.EXPIRATION_DELTA);
        tooFarAwayAd = AdvertisementManager.createPeerAdvertisement(new Identity(0x4, "farAway", (PublicKey) null), 0, AdvertisementManager.EXPIRATION_DELTA);
        tooFarAwayAd.setHopCount(3);
        
        newPeers = new PeerAdvertisement[]{ ourPeerAd, peer2Ad, peer3Ad, tooFarAwayAd};
	}
    
    public final void testAddAdvertisement(){
        assertEquals(0, adManager.countRemoteAdvertisement());
        assertEquals(1, adManager.getPeerAdvertisementArray().length);
        adManager.addAdvertisement(newPeers);
        assertEquals(2, adManager.countRemoteAdvertisement());
    }
    
    public final void testGetPeerAdvertisement() {
        assertEquals(0, newPeers[1].getHopCount());
        assertNull(adManager.getPeerAd(newPeers[1].peerId));
        adManager.addAdvertisement(newPeers);
        PeerAdvertisement ad = (PeerAdvertisement) adManager.getPeerAd(newPeers[1].peerId);
        assertNotNull(ad);
//        assertNotSame(newPeers[1], ad);
//        assertNotSame(newPeers[1].peerId, ad.peerId);

        assertEquals(newPeers[1].peerId, ad.peerId);
        assertEquals(newPeers[1].getIpAddress(), ad.getIpAddress());

        assertEquals(1, ad.getHopCount());
    }

    public final void testGetPeerAdvertisementArray() {
        PeerAdvertisement[] peerArray1 = adManager.getPeerAdvertisementArray();
        long oldTimeStamp = peerArray1[0].getExpiration();
        assertEquals(1, peerArray1.length);
        adManager.addAdvertisement(newPeers);
        PeerAdvertisement[] peerArray2 = adManager.getPeerAdvertisementArray();
        
        assertTrue(oldTimeStamp <= peerArray2[0].getExpiration());
        assertEquals(3, peerArray2.length);
        for(int i=0; i< peerArray2.length; i++){
            assertNotNull(peerArray2[i]);
        }
    }
	
    /*
	public final void testSaveManager(){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			adManager.saveTo(baos);
			AdvertisementManager adClone = AdvertisementManager.loadFrom(new ByteArrayInputStream(baos.toByteArray()));
			
			assertEquals(adManager.countRemoteAdvertisement(), adClone.countRemoteAdvertisement());
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
    */

}

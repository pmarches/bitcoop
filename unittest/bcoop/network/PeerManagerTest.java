/**
 * <p>Title: PeerManagerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

import java.security.PublicKey;

import bcoop.identity.Identity;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class PeerManagerTest extends TestCase {
    PeerManager peerMan;
    PeerInformation peerInfo1 = new PeerInformation(new Identity(0x1, "peer1", (PublicKey) null));
    PeerInformation peerInfo2 = new PeerInformation(new Identity(0x2, "peer2", (PublicKey) null));
    
    public void setUp(){
        peerMan = new PeerManager();
    }
    
    public final void testAddPeer() {
    	assertEquals(0, peerMan.getModifiedCount());
        peerMan.addPeer(peerInfo1);
    	assertEquals(1, peerMan.getModifiedCount());

        Identity[] allPeers = peerMan.getAllPeers();
        assertNotNull(allPeers);
        assertEquals(1, allPeers.length);
        assertEquals(peerInfo1.getPeerId(), allPeers[0]);
    }

    public final void testHasRemotePeers() {
        assertFalse(peerMan.hasRemotePeers());
        peerMan.addPeer(peerInfo1);
        assertTrue(peerMan.hasRemotePeers());
    }

    public final void testGetRandomPeerAdvertisement() {
        assertNull(peerMan.getRandomPeerAdvertisement());
        peerMan.addPeer(peerInfo1);
        PeerInformation randomPeer = peerMan.getRandomPeerAdvertisement();
        assertNotNull(randomPeer);
        assertEquals(randomPeer.getPeerId(), peerInfo1.getPeerId());

        peerMan.addPeer(peerInfo2);
        boolean gotPeer1 = false;
        boolean gotPeer2 = false;
        while(gotPeer1 == false && gotPeer2 == false){
            randomPeer = peerMan.getRandomPeerAdvertisement();
            assertNotNull(randomPeer);
            if(randomPeer.getPeerId().equals(peerInfo1.getPeerId())){
                gotPeer1 = true;
            }
            else if(randomPeer.getPeerId().equals(peerInfo2.getPeerId())){
                gotPeer2 = true;
            }
            else{
                fail();
            }
        }
    }

    public final void testGetPeerAd() {
        assertNull(peerMan.getPeerAd(peerInfo1.getPeerId()));
        peerMan.addPeer(peerInfo1);
        assertNotNull(peerMan.getPeerAd(peerInfo1.getPeerId()));
        assertEquals(peerInfo1.getPeerId(), peerMan.getPeerAd(peerInfo1.getPeerId()).getPeerId());
    }

    public final void testCountRemoteAdvertisement() {
        assertEquals(0, peerMan.countRemoteAdvertisement());
        peerMan.addPeer(peerInfo1);
        assertEquals(1, peerMan.countRemoteAdvertisement());
    }
    
    public final void testRemovePeer() throws InterruptedException{
        peerMan.addPeer(peerInfo1);
		long timeStamp = peerMan.getModifiedCount();
		Thread.sleep(15); //Delay at least 15 for windows..
		Thread.yield();
        peerMan.removePeer(peerInfo1.getPeerId());
        assertTrue(timeStamp +" "+ peerMan.getModifiedCount(), timeStamp < peerMan.getModifiedCount());
    }

    class PeerAdder extends Thread{
        public void run(){
            peerMan.addPeer(peerInfo1);
        }
    }
    public final void testWaitForPeer() {
        try{
            PeerAdder peerAdder = new PeerAdder();
            peerAdder.start();
            peerMan.waitForPeer(1);
            assertEquals(1, peerMan.countRemoteAdvertisement());
    
            peerAdder.join();
        }
        catch(InterruptedException e){
            e.printStackTrace();
            fail();
        }
    }

}

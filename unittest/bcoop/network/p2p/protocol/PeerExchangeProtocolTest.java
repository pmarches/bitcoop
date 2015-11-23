/**
 * <p>Title: PeerExchangeProtocolTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.net.Socket;
import java.security.PublicKey;

import bcoop.identity.Identity;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.network.p2p.advertisement.PeerAdvertisement;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class PeerExchangeProtocolTest extends TestCase {
	AdvertisementManager fullAdMan ;
	AdvertisementManager emptyAdMan;
	
	public void setUp(){
		PeerAdvertisement thirdPeer = AdvertisementManager.createPeerAdvertisement(new Identity(0x3, "toto", (PublicKey) null), 0, AdvertisementManager.EXPIRATION_DELTA);

		fullAdMan = new AdvertisementManager(AdvertisementManager.createPeerAdvertisement(new Identity(0x1, "peer1", (PublicKey) null), 0, AdvertisementManager.EXPIRATION_DELTA));
		fullAdMan.addPeer(thirdPeer);
		emptyAdMan = new AdvertisementManager(AdvertisementManager.createPeerAdvertisement(new Identity(0x2, "peer2", (PublicKey) null), 0, AdvertisementManager.EXPIRATION_DELTA));
	}

	public final void testPropagateFullToEmpty() {
		try{
            assertEquals(1, fullAdMan.countRemoteAdvertisement());
            assertEquals(0, emptyAdMan.countRemoteAdvertisement());

            LoopSocketFactory loopSocket = new LoopSocketFactory();
            Socket socket1 = loopSocket.getSocket();
            Socket socket2 = loopSocket.getSocket();

			PeerExchangeProtocol peerXChangeFull = new PeerExchangeProtocol(fullAdMan, socket1.getInetAddress().getHostAddress(), socket1.getInputStream(), socket1.getOutputStream());
			PeerExchangeProtocol peerXChangeEmpty = new PeerExchangeProtocol(emptyAdMan, socket2.getInetAddress().getHostAddress(), socket2.getInputStream(), socket2.getOutputStream());
			
			ProtocolRunner runner1 = new ProtocolRunner(peerXChangeFull, true);
			ProtocolRunner runner2 = new ProtocolRunner(peerXChangeEmpty, true);
			runner1.start();
			runner2.start();
			runner1.join();
			runner2.join();

			assertEquals(2, fullAdMan.countRemoteAdvertisement());
            assertEquals(2, emptyAdMan.countRemoteAdvertisement());
			
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
}

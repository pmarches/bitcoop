package bcoop.network;

import java.io.IOException;
import java.security.PublicKey;

import mockObject.MockIdentityManager;

import bcoop.identity.Identity;

import junit.framework.TestCase;

public class PeerVisitorTest extends TestCase implements VisitorAction {

	private int nbCall;

	/*
	 * Test method for 'bcoop.network.PeerVisitor.visitAll(VisitorAction)'
	 */
	public void testVisitAllBasic() {
		try{
			PeerManager pMan = new PeerManager();
			PeerVisitor visitor = new PeerVisitor(pMan);
			nbCall = 0;
			visitor.visitAll(this);
			assertEquals(0, nbCall);
			
			PeerInformation peer1 = new PeerInformation(new Identity(0x1, "peer1", (PublicKey) null));
			pMan.addPeer(peer1);
			nbCall = 0;
			visitor.visitAll(this);
			assertEquals(1, nbCall);
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

	public void testVisitAllMultiThreadWithModifications() {
		try{
			PeerManager pMan = new PeerManager();
			pMan.addPeer(new PeerInformation(MockIdentityManager.PEER_ID1));
			pMan.addPeer(new PeerInformation(MockIdentityManager.PEER_ID2));

			NotifyThreadAction tAction = new NotifyThreadAction();
			PeerVisitor visitor = new PeerVisitor(pMan);
			VisitorThread vThread = new VisitorThread(visitor, tAction);
			synchronized (tAction){
				vThread.start();
				assertNull(tAction.lastVisitedPeer);
				tAction.wait();
				assertEquals(MockIdentityManager.PEER_ID2, tAction.lastVisitedPeer);
				pMan.addPeer(new PeerInformation(MockIdentityManager.PEER_ID3));
				pMan.removePeer(tAction.lastVisitedPeer);
				tAction.lastVisitedPeer = null;
				tAction.notifyAll();

				tAction.wait();
				assertEquals(MockIdentityManager.PEER_ID1, tAction.lastVisitedPeer);
				tAction.lastVisitedPeer = null;
				tAction.notifyAll();

				tAction.wait();
				assertEquals(MockIdentityManager.PEER_ID3, tAction.lastVisitedPeer);
				tAction.lastVisitedPeer = null;
				tAction.notifyAll();
			}
			vThread.join();
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	class VisitorThread extends Thread{
		private PeerVisitor visitor;
		private NotifyThreadAction tAction;
		
		public VisitorThread(PeerVisitor visitor, NotifyThreadAction tAction){
			this.visitor = visitor;
			this.tAction = tAction;
		}
		
		public void run(){
			try {
				synchronized(this.tAction){
					visitor.visitAll(tAction);
					
					this.tAction.notifyAll(); //Final notification
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void visit(PeerInformation peerInfo) throws IOException {
		nbCall++;
	}
	
	class NotifyThreadAction implements VisitorAction{
		public Identity lastVisitedPeer;

		synchronized public void visit(PeerInformation peerInfo) throws IOException {
			lastVisitedPeer = peerInfo.getPeerId();
			this.notifyAll();
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}

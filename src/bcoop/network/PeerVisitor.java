package bcoop.network;

import java.io.IOException;
import java.util.HashSet;

import bcoop.identity.Identity;

public class PeerVisitor {
	private PeerManager peerMan;

	public PeerVisitor(PeerManager peerMan){
		this.peerMan = peerMan;
	}
	
	public void visitAll(VisitorAction action) throws IOException{
		HashSet<PeerInformation> visitedPeers = new HashSet<PeerInformation>();
		long modifiedCountAtStart=0;
		do{
			modifiedCountAtStart = peerMan.getModifiedCount();
			Identity[] peers = peerMan.getAllPeers();

			for(Identity peerName : peers){
				PeerInformation peerInfo = peerMan.getPeerAd(peerName);
				if(peerInfo != null && !visitedPeers.contains(peerInfo)){
					visitedPeers.add(peerInfo);
					action.visit(peerInfo);
				}
			}
		}
		while(modifiedCountAtStart != peerMan.getModifiedCount());

	}
}

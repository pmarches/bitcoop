package bcoop.restoration;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import bcoop.block.HeaderBlock;
import bcoop.exception.ConnectionRefusedException;
import bcoop.exception.StreamClosedException;
import bcoop.identity.Identity;
import bcoop.network.Connection;
import bcoop.network.Network;
import bcoop.network.PeerInformation;
import bcoop.network.PeerManager;
import bcoop.network.PeerVisitor;
import bcoop.network.VisitorAction;
import bcoop.util.HashMatrix;

public class BackupDiscovery implements VisitorAction {
	private PeerManager peerMan;
	private Network network;
	private HashMatrix<String, Identity, HeaderBlock> discoveredHeaderBlock;
	private Vector<Pattern> regexToMatch = new Vector<Pattern>();

	public BackupDiscovery(PeerManager peerMan, Network network){
		this.peerMan = peerMan;
		this.network = network;
	}
	
	public HashMatrix<String, Identity, HeaderBlock> discoverHeaderBlocks() throws IOException{
		PeerVisitor visitor = new PeerVisitor(this.peerMan);
		this.discoveredHeaderBlock = new HashMatrix<String, Identity, HeaderBlock>();
		visitor.visitAll(this);
		return this.discoveredHeaderBlock;
	}

	public void visit(PeerInformation peerInfo){
		try{
			Connection pConn = this.network.getConnection(peerInfo.getPeerId());
			Vector<HeaderBlock> hBlockList = pConn.requestAllHeaderBlock();
			for(HeaderBlock hBlock : hBlockList){
				if(matchesFilter(hBlock.getFilename())){
					Logger.getLogger(this.getClass()).info("Discovered "+hBlock.getFilename());
					this.discoveredHeaderBlock.put(hBlock.getFilename(), peerInfo.getPeerId(), hBlock);
				}
			}
		} catch (ConnectionRefusedException e) {
			Logger.getLogger(this.getClass()).warn("Could not connect to peer "+peerInfo.getPeerId());
		}
		catch (StreamClosedException e) {
			Logger.getLogger(this.getClass()).error("Peer "+peerInfo.getPeerId()+ " closed the stream unexpectedly");
		}
		catch(IOException e){
			//Ok, we simply skip this peer if we cannot connect.
		}
	}

	private boolean matchesFilter(String filename) {
		if(regexToMatch.isEmpty()) return true;
		
		for(Pattern pattern : regexToMatch){
			Matcher match = pattern.matcher(filename);
			if(match.matches()){
				return true;
			}
		}
		return false;
	}
	
	public void addMatchFilter(String regex){
		regexToMatch.add(Pattern.compile(regex));
	}
}

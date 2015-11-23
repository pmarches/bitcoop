package bcoop.backup;

import java.util.Vector;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import bcoop.block.HeaderBlock;
import bcoop.block.NumberedBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.exception.NoPeerAvailableException;
import bcoop.identity.Identity;
import bcoop.network.PeerInformation;
import bcoop.network.PeerManager;
import bcoop.repos.RepositoryManager;
import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 * This class should determine the best peer to send a block to. Basic rules are this:
 * - A peer shoud be able to contain a whole file. This way, if a host fails, we can alway retrieve some
 *   of the files in the transaction, but all the files retrieved are complete.
 * - If no peer can contain the whole file, then it is distributed among the smallest number of peers possible.
 * 
 * - Replication of blocks must be done at the same time as the sending of blocks to the first host. The reason
 *   is that the file might change and then the block content could be corrupted.
 *   
 * - 
 */
public class DestinationChooser {
	private PeerManager peerManager;
    private BlockTracker bTracker;
    private RepositoryManager repoManager;

    public DestinationChooser(PeerManager peerManager, BlockTracker bTracker, RepositoryManager repoManager){
    		if(peerManager == null){
    			throw new RuntimeException("peerManager cannot be null");
    		}
    		this.peerManager = peerManager;

    		if(bTracker == null){
    			throw new RuntimeException("bTracker cannot be null");
    		}
    		this.bTracker = bTracker;

    		if(repoManager == null){
    			throw new RuntimeException("repoManager cannot be null");
    		}
    		this.repoManager = repoManager;
    }
    
	public Identity getDestinationForCompleteFile(BitCoopFile file) throws NoPeerAvailableException{
		if(file == null) throw new NoPeerAvailableException();
		
		long totalFreeSpaceOnAllRemote = this.repoManager.getTotalAllowedSpaceForAllPeers()-this.repoManager.getLocalSpaceUsedByAllPeers();
		if(totalFreeSpaceOnAllRemote < file.length()){
			Logger.getLogger(this.getClass()).warn("Local repository is not big enough to allow sending of file "+file.getAbsolutePath());
			throw new NoPeerAvailableException();
		}
		
		try{
			Identity peerId = getLastDestinationPeer(file);		
			if(peerId != null && canPeerStoreSize(peerId, file.length())){
				return peerId;
			}
			return getPeerThatCanStoreSize(file.length());
		}
		catch(NoPeerAvailableException e){
			Logger.getLogger(this.getClass()).info("Unable to find a single peer to store the complete file "+file.getAbsolutePath()+". File is too big ("+file.length()+") so we break it up.");
		}
		
		return null;
	}
	
	private Identity getPeerThatCanStoreSize(long dataSize) throws NoPeerAvailableException{
		HashSet<Identity> alreadyTriedPeers = new HashSet<Identity>();
		while(true){
			Identity peerId = getBestPeerNotInSet(alreadyTriedPeers);
			if(peerId == null){
				throw new NoPeerAvailableException();
			}
			if(canPeerStoreSize(peerId, dataSize)){
				return peerId;
			}
			alreadyTriedPeers.add(peerId);
		}
	}
	
    private boolean canPeerStoreSize(Identity peerId, long dataSize) {
   		return this.repoManager.canStoreBlockSizeRemotely(peerId, dataSize);
	}

	private Identity getBestPeerNotInSet(HashSet<Identity> excludedPeers) throws NoPeerAvailableException {
			Identity bestPeer=null;
    		long bestScore = Integer.MAX_VALUE;
    		for(Identity peer : peerManager.getAllPeers()){
    			if(excludedPeers.contains(peer)) continue;

    			long score = getScore(peer);
    			if(score < bestScore){
    				bestScore = score;
    				bestPeer = peer;
    			}
    		}
    		if(bestPeer==null){
    			throw new NoPeerAvailableException();
    		}
		PeerInformation peerInfo = peerManager.getPeerAd(bestPeer);
		if(peerInfo == null){
			return null;
		}
		return peerInfo.getPeerId();
	}

	private long getScore(Identity peer) {
		long nbOwedToMe = this.repoManager.getRepositoryForPeer(peer).getUsedSpace();
		long nbIOweToPeer = this.bTracker.getBytesStoredOn(peer);
		return nbOwedToMe-nbIOweToPeer;
	}

	private Identity getLastDestinationPeer(BitCoopFile file){
		if(file == null){
			Logger.getLogger(this.getClass()).warn("Cannot getLastDestinationPeer for null file. Returning null.");
			return null;
		}
        HeaderBlock hBlock = this.bTracker.getLatestHeaderBlockForFile(file.getAbsolutePath());
        if(hBlock == null){ //First time we backup this file..
            return null;
        }
        LinkedList<Long> blocks = hBlock.getAssociatedDataBlockId();
        if(blocks == null || blocks.isEmpty()) return null;

        Vector<Identity> peers = this.bTracker.getPeerListForBlock(blocks.getLast());
        if(peers.isEmpty()) return null;
        return peers.get(0);
    }

	public Identity getDestination(NumberedBlock block) throws NoPeerAvailableException {
		return getPeerThatCanStoreSize(block.getStorageSizeOfBlock());
	}

}

/**
 * <p>Title: PeerManager.java</p>
 * <p>Description: Stores peer information.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import bcoop.identity.Identity;


/**
 * @author pmarches
 *
 */
public class PeerManager {
    protected Hashtable<Identity, PeerInformation> remotePeerInformation;
    Random rng;
	private int modifiedCount;

	public PeerManager(){
		this.rng = new Random();
		this.remotePeerInformation = new Hashtable<Identity, PeerInformation>();
		this.modifiedCount = 0;
	}

    synchronized public void addPeer(PeerInformation newPeer){
		if(remotePeerInformation.containsKey(newPeer.getPeerId()) == false){
			Logger.getLogger(this.getClass()).debug("Adding peer "+newPeer.peerId+" to peer manager");
			remotePeerInformation.put(newPeer.getPeerId(), newPeer);
		}
        this.modifiedCount++;
        notifyAll();
    }

    public Identity[] getAllPeers() {
    	Identity[] allPeerIds = new Identity[this.remotePeerInformation.size()];
        Set<Identity> keys = this.remotePeerInformation.keySet();
        int i=0;
        for(Identity peerId : keys){
            allPeerIds[i] = peerId;
            i++;
        }
        return allPeerIds;
    }

    public boolean hasRemotePeers(){
        return countRemoteAdvertisement() > 0;
    }
    
    /**
     * @return
     * @throws NoPeerAvailableException 
     */
    public PeerInformation getRandomPeerAdvertisement() {
        if(!hasRemotePeers()){
            return null;
        }
        int random = rng.nextInt(remotePeerInformation.size());
        return getPeerAdvertisement(random);
    }

    /**
     * @param peerDestinationId
     * @return
     */
    public PeerInformation getPeerAd(Identity peerDestinationId) {
	    	if(peerDestinationId == null){
	    		return null;
	    	}
        return remotePeerInformation.get(peerDestinationId);
    }

    public int countRemoteAdvertisement() {
        return remotePeerInformation.size();
    }

    synchronized public void waitForPeer(int nbPeersRequired) {
        while(this.getNumberOfRemotePeers() < nbPeersRequired) {
            try {
            		Logger.getLogger(getClass()).debug("Waiting for more peer "+remotePeerInformation.size());
            		wait(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }

	private PeerInformation getPeerAdvertisement(int advertisementNumber) {
        Set keys = remotePeerInformation.keySet();
        int i=0;
        for(Object key : keys){
            if(i == advertisementNumber){
                return remotePeerInformation.get(key);
            }
            i++;
        }
        return null;
	}

	public void removePeer(Identity peerToRemove) {
		remotePeerInformation.remove(peerToRemove);
        this.modifiedCount++;
	}

	public long getModifiedCount() {
		return modifiedCount;
	}

	public int getNumberOfRemotePeers() {
		return remotePeerInformation.size();
	}
}

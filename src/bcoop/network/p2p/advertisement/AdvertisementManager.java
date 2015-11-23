/**
 * <p>Title: AdvertisementManager.java</p>
 * <p>Description: Receives and sends advertisements about other hosts</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.advertisement;

import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import bcoop.identity.Identity;
import bcoop.network.PeerManager;
import bcoop.network.PeerInformation;

/**
 * @author pmarches
 *
 */
public class AdvertisementManager extends PeerManager implements Serializable {
	private static final long serialVersionUID = 5584552790243786236L;
	protected static final int MAX_HOP_COUNT = 3;
	transient private PeerAdvertisement ourAdvertisement;
	public static final int EXPIRATION_DELTA = 2*60*60*1000; //Two hours
	
	public static PeerAdvertisement createPeerAdvertisement(Identity peerId, int port, int expirationDelta){
		PeerAdvertisement advertisement = new PeerAdvertisement(peerId);
		advertisement.setPort(port);
		advertisement.setExpirationTime (System.currentTimeMillis()+expirationDelta);
		
		return advertisement;
	}
	
	public AdvertisementManager(PeerAdvertisement ourAdvertisement){
		if(ourAdvertisement == null){
			throw new RuntimeException("Our advertisement cannot be null.");
		}
		this.ourAdvertisement = ourAdvertisement;
	}
	
	/**
	 * @return
	 */
	public PeerAdvertisement[] getPeerAdvertisementArray() {
		if(ourAdvertisement == null){
			throw new RuntimeException("Invalid state of the PeerAdvertisementManager");
		}
		PeerAdvertisement[] array = new PeerAdvertisement[remotePeerInformation.size()+1];
		array[0] = getLocalPeerAdvertisement();
		Set keys = remotePeerInformation.keySet();
		int i=1;
		for(Object key : keys){
			array[i] = (PeerAdvertisement) remotePeerInformation.get(key);
			i++;
		}
		return array;
	}
	
	public synchronized void addPeer(PeerInformation newPeer) {
		if(newPeer == null){
			Logger.getLogger(this.getClass()).error("Got a null newPeer? Ignoring it.");
			return;
		}
		PeerAdvertisement ad = (PeerAdvertisement) newPeer;
		if(allowAdvertisementIntoCache(ad)){
			ad.incrementHopCount();
			super.addPeer(ad);
		}		
	}
	
	
	/**
	 * @param newPeers
	 */
	public void addAdvertisement(PeerAdvertisement[] advertisements) {
		if(advertisements == null){
			Logger.getLogger(this.getClass()).error("We got a Null advertisement array. It should containt at least one advetrtisement (the remote peer's)");
			return;
		}
		for(PeerAdvertisement peerAd : advertisements){
			addPeer(peerAd);
		}
	}
	
	/*junit*/ boolean allowAdvertisementIntoCache(PeerAdvertisement ad) {
		if(ourAdvertisement.peerId.equals(ad.getPeerId())){
			Logger.getLogger(this.getClass()).debug("Got our own advertisement "+ ad);
			return false;
		}
		//TODO CHeck for negative hopcount..
		if(ad.getHopCount() >= MAX_HOP_COUNT){
			Logger.getLogger(this.getClass()).debug("Got a big hopCount "+ ad);
			return false;
		}
		//TODO Check for the maximum advertisement time. 
		if(ad.getExpiration() <= System.currentTimeMillis()){
			Logger.getLogger(this.getClass()).debug("Got an old advertisement ("+ad+") now is:"+System.currentTimeMillis());
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 */
	void removeExpiredAdvertisements() {
		long now = System.currentTimeMillis();
		Iterator it = remotePeerInformation.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Entry) it.next();
			PeerAdvertisement peerAd = (PeerAdvertisement) entry.getValue();
			if(peerAd.getExpiration() < now){
				it.remove();
			}
		}
	}
	
	public PeerAdvertisement getLocalPeerAdvertisement() {
		ourAdvertisement.setExpirationTime(System.currentTimeMillis() + EXPIRATION_DELTA);
		return ourAdvertisement;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException{
		out.writeObject(this.ourAdvertisement);
		out.writeObject(remotePeerInformation);
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
		this.ourAdvertisement = (PeerAdvertisement) in.readObject();
		this.remotePeerInformation = (Hashtable<Identity, PeerInformation>) in.readObject();
	}
	
}

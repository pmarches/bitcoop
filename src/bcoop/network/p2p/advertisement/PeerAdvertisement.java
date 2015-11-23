/**
 * <p>Title: PeerAdvertisement.java</p>
 * <p>Description: information how to contact a peer</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.advertisement;

import bcoop.identity.Identity;
import bcoop.network.PeerInformation;

/**
 * @author pmarches
 *
 */
public class PeerAdvertisement extends PeerInformation implements Advertisement {
	private static final long serialVersionUID = 8079167682211495998L;
	private int hopCount;
	private long expirationTime;
    private String ipAddress;
    private int port;
	
	public PeerAdvertisement(){
        initDefault();
	}

	public PeerAdvertisement(Identity peerId) {
		super(peerId);
        initDefault();
	}
    
    private void initDefault() {
        hopCount = 0;
        expirationTime = System.currentTimeMillis()+ AdvertisementManager.EXPIRATION_DELTA;
    }
    
    public boolean equals(Object obj){
    	if(obj == null) return false;
    	if(!(obj instanceof PeerAdvertisement)){
    		return false;
    	}
    	PeerAdvertisement otherAd = (PeerAdvertisement) obj;
    	if(this.getPeerId().equals(otherAd.getPeerId())==false) return false;
    	if(this.getIpAddress() != null && this.getIpAddress().equals(otherAd.getIpAddress())==false) return false;
    	if(this.getPort()!=otherAd.getPort()) return false;
//    	if(this.getExpirationTime() != otherAd.getExpirationTime()) return false;
    	if(this.getHopCount() != otherAd.getHopCount()) return false;
    	
    	return true;
    }

/*
    public Object clone() throws CloneNotSupportedException{
        PeerAdvertisement clone = (PeerAdvertisement) super.clone();
        clone.hopCount = hopCount;
        clone.expirationTime = expirationTime;
        if(ipAddress != null) clone.ipAddress = new String(ipAddress);
        clone.port = port;

        return clone;
    }
*/
    /* (non-Javadoc)
	 * @see bcoop.network.p2p.advertisement.Advertisement#getExpiration()
	 */
	public long getExpiration() {
		return expirationTime;
	}

	/* (non-Javadoc)
	 * @see bcoop.network.p2p.advertisement.Advertisement#getHopCount()
	 */
	public int getHopCount() {
		return hopCount;
	}
    
    synchronized public String toString(){
        return String.format("ID=%s IP=%s port=%s hop=%s expire=%s", new Object[]{peerId, ipAddress, port, hopCount, expirationTime});
    }

	public long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHopCount(int hopCount) {
		this.hopCount = hopCount;
	}

	public void incrementHopCount() {
		this.hopCount++;
	}
}

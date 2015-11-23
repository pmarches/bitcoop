/**
 * <p>Title: PeerInformation.java</p>
 * <p>Description: TODO What is this??? Basic Information about a peer</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

import java.io.Serializable;

import bcoop.identity.Identity;
import bcoop.identity.LocalIdentity;

/**
 * @author pmarches
 */
public class PeerInformation implements Serializable {
	private static final long serialVersionUID = 8851375538684104236L;
	public Identity peerId;
	
	public PeerInformation(){
	}

	public PeerInformation(Identity peerId){
		setIdentity(peerId);
	}
	
	protected void setIdentity(Identity peerId){
		if(peerId instanceof LocalIdentity){ //We do not allow the publication if localIdentity
			peerId = peerId.getBaseIdentity();
		}
		this.peerId = peerId;
	}

	public Identity getPeerId() {
		return peerId;
	}

	/*
	public Object clone() throws CloneNotSupportedException{
		PeerInformation clone = (PeerInformation) super.clone();
		if(peerId != null) clone.peerId = peerId;
		
		return clone;
	}
*/
}

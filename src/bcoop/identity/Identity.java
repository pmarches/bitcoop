package bcoop.identity;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.InvalidKeyException;
import java.security.PublicKey;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import sun.security.provider.DSAPublicKey;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Identity implements Comparable<Identity>, Externalizable {
	private static final long serialVersionUID = -304527470448128202L;

	@Id
	private long uniqueID;
	private String humanReadableAlias;
	PublicKey publicKey;

	public Identity(){
//		throw new RuntimeException("Identity public CTOR SHOULD NOT BE CALLED!");
	}

	private void init(long uniqueID, String humanReadableAlias, PublicKey publicKey) {
		if(uniqueID == 0){
			throw new RuntimeException("Identity ID cannot be 0");
		}
		this.uniqueID = uniqueID;
		this.humanReadableAlias = humanReadableAlias;
		this.publicKey = publicKey;
	}

	public Identity(long uniqueID, String humanReadableAlias, PublicKey publicKey) {
		init(uniqueID, humanReadableAlias, publicKey);
	}
	
	public Identity(long uniqueID, String humanReadableAlias, byte[] publicPEM) throws InvalidKeyException {
		PublicKey publicKey = fromPEMPublicKeyBytes(publicPEM);
		init(uniqueID, humanReadableAlias, publicKey);
	}

	public int compareTo(Identity arg0) {
		Identity arg = (Identity) arg0;
		if(arg == null) return -1;
		return this.getUniqueId().compareTo(arg.getUniqueId());
	}
	
	public boolean equals(Identity id2){
		if(this.compareTo(id2) != 0){
			return false;
		}
		if(this.publicKey==null){
			if(((Identity)id2).publicKey==null){
				return true;
			}
			return false;
		}
		return this.publicKey.equals(((Identity)id2).publicKey);
	}

	public Identity getBaseIdentity() {
		return new Identity(this.getUniqueId(), this.getHumanReadableAlias(), this.publicKey);
	}

	public Long getUniqueId() {
		return this.uniqueID;
	}
	
	public String getUniqueIdString(){
		return Long.toHexString(getUniqueId());
	}
	
	public PublicKey getVerificationKey(){
		return this.publicKey;
	}
	
	public int hashCode(){
		return (int) this.uniqueID;
	}

	public String getHumanReadableAlias() {
		return this.humanReadableAlias;
	}
	
	public String toString(){
		return "IdentityID:0x"+Long.toHexString(this.uniqueID)+" ("+this.humanReadableAlias+") ";
	}
	
	private static PublicKey fromPEMPublicKeyBytes(byte[] publicPEM) throws InvalidKeyException {
		if(publicPEM == null){
			return null;
		}
		return new DSAPublicKey(publicPEM);
	}

	public byte[] getPEMPublicKeyBytes() throws IOException{
		if(this.publicKey == null){
			return null;
		}
		return this.publicKey.getEncoded();
	}
	
	public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
		this.uniqueID = in.readLong();
		this.humanReadableAlias = (String) in.readObject();
		
		byte[] pubKeybytes = (byte[]) in.readObject();
		try {
			this.publicKey = fromPEMPublicKeyBytes(pubKeybytes);
		} catch (InvalidKeyException e) {
			throw new IOException("Bad PEM Public key bytes");
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(this.uniqueID);
		out.writeObject(this.humanReadableAlias);
		out.writeObject(this.getPEMPublicKeyBytes());
	}
}

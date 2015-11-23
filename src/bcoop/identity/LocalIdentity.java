package bcoop.identity;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import sun.security.provider.DSAPrivateKey;

@Entity
public class LocalIdentity extends Identity {
	private static final long serialVersionUID = 4444721715780488983L;
	
	SecretKey encryptionKey; //Real encryption key. Faster than public key encryption for big files..
	PrivateKey privateKey;

	public LocalIdentity(){
		super();
	}

	public LocalIdentity(long uniqueId, String humanReadableAlias, byte[] publicPEM, byte[] privatePEM, byte[] symetricPEMKey) throws InvalidKeyException{
		super(uniqueId, humanReadableAlias, publicPEM);
		this.privateKey = fromPEMPrivateKeyBytes(privatePEM);
		this.encryptionKey = new SecretKeySpec(symetricPEMKey, "AES");
	}

	public LocalIdentity(long uniqueId, String humanReadableAlias, PublicKey publicKey, PrivateKey privateKey, SecretKey encryptionKey){
		super(uniqueId, humanReadableAlias, publicKey);
		this.privateKey = privateKey;
		this.encryptionKey = encryptionKey;
	}
	
	public PrivateKey getSigningKey(){
		return privateKey;
	}
	
	/**
	 * @return the symetric encryption key used for file encryption
	 */
	public SecretKey getEncryptionKey(){
		return encryptionKey;
	}
	
	public String toString(){
		return "Local"+super.toString();
	}
	
	public boolean equals(Object obj){
		if(obj == null) return false;
		if(obj instanceof LocalIdentity){
			if(super.equals(obj) == false) return false;
			
			LocalIdentity id2 = (LocalIdentity) obj;
			if(this.encryptionKey != null && this.encryptionKey.equals(id2.encryptionKey) == false) return false;
			if(this.privateKey != null && this.privateKey.equals(id2.privateKey)==false) return false;
			
			return true;
		}
		return false;
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		
		//Get the PEM bytes
		byte[] pemBytes = (byte[]) in.readObject();
		try {
			this.privateKey = fromPEMPrivateKeyBytes(pemBytes);
		} catch (InvalidKeyException e) {
			throw new IOException("Cant read bad Private PEM bytes");
		}
		
		byte[] secretBytes = (byte[]) in.readObject();
		this.encryptionKey = new SecretKeySpec(secretBytes, "AES");
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		//FIXME Find a way to prevent this object to be sent over the internet. disk is Ok, but not the net.
		super.writeExternal(out);
		
		out.writeObject(this.getPEMPrivateKeyBytes());
		if(this.encryptionKey == null){
			out.writeObject(null);
		}
		else{
			out.writeObject(this.encryptionKey.getEncoded());
		}
	}

	private PrivateKey fromPEMPrivateKeyBytes(byte[] privatePEM) throws InvalidKeyException {
		return new DSAPrivateKey(privatePEM);
	}
	
	public byte[] getPEMPrivateKeyBytes() throws IOException {
		if(this.privateKey == null){
			return null;
		}
		return this.privateKey.getEncoded();
	}

	public byte[] getPEMSymmetricKeyBytes() {
		return this.encryptionKey.getEncoded();
	}

}

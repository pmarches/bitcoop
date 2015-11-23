package bcoop.crypto;

import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

public class Encryptor {
	private SecretKey secretKey;
	public Cipher encrpytionCypher;

	public Encryptor(SecretKey secretKey){
		try{
			this.secretKey = secretKey;
			this.encrpytionCypher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			this.encrpytionCypher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void reset(){
		try {
			this.encrpytionCypher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] encrypt(byte[] clearText) throws IllegalBlockSizeException, BadPaddingException{
		return this.encrpytionCypher.doFinal(clearText);
	}

	public byte[] getIV() {
		return this.encrpytionCypher.getIV();
	}
}

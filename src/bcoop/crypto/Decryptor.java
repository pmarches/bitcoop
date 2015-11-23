package bcoop.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Decryptor {

	private SecretKey secretKey;
	private Cipher decryptionCypher;

	public Decryptor(SecretKey secretKey, byte[] iv) {
		try{
			this.secretKey = secretKey;
			this.decryptionCypher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
			this.decryptionCypher.init(Cipher.DECRYPT_MODE, this.secretKey, ivParamSpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	public byte[] decrypt(byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException {
		return this.decryptionCypher.doFinal(cipherText);
	}

}

package bcoop.identity;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.DSAParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class IdentityGenerator {
	KeyPairGenerator dsaKpg;
	KeyGenerator aesKeyGen;
	private SecureRandom secureRandom;
	
	public IdentityGenerator(DSAParameterSpec dsaParams){
		try {
			this.secureRandom = new SecureRandom();
			this.dsaKpg = KeyPairGenerator.getInstance("DSA");
			if(dsaParams != null){
				try {
					this.dsaKpg.initialize(dsaParams); //this should be used only for unit testing.
				} catch (InvalidAlgorithmParameterException e) {
					e.printStackTrace();
				}
			}
			else{
				this.dsaKpg.initialize(1024, this.secureRandom);
			}

	        int keySize=128;
	        if(Cipher.getMaxAllowedKeyLength("AES")==Integer.MAX_VALUE){
	        	keySize = 256;
	        }
			this.aesKeyGen = KeyGenerator.getInstance("AES");
			this.aesKeyGen.init(keySize, this.secureRandom);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public LocalIdentity generateLocalIdentity(String humanReadableAlias){
		KeyPair dsaKp = dsaKpg.generateKeyPair();
		long keyId = secureRandom.nextLong();
		PrivateKey privateKey = dsaKp.getPrivate();
		PublicKey publicKey = dsaKp.getPublic();
		SecretKey aesKey = aesKeyGen.generateKey();

		LocalIdentity identity = new LocalIdentity(keyId, humanReadableAlias, publicKey, privateKey, aesKey);
		return identity;
	}
}

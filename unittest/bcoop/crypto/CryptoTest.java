package bcoop.crypto;

import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import mockObject.MockBlocks;
import mockObject.MockIdentityManager;
import junit.framework.TestCase;

public class CryptoTest extends TestCase {
	SecretKey secretKey = MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey();

	public void testEncryptDecryptBlock() throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
		Encryptor encryptor = new Encryptor(secretKey);

		byte[] encrypted0 = encryptor.encrypt(MockBlocks.CLEAR_DATABLOCK[0].getBlockData());
		byte[] encrypted1 = encryptor.encrypt(MockBlocks.CLEAR_DATABLOCK[1].getBlockData());
		
		assertNotSame(encrypted0, MockBlocks.CLEAR_DATABLOCK[0].getBlockData());
		assertTrue(MockBlocks.CLEAR_DATABLOCK[0].getBlockData().length < encrypted0.length);
		assertFalse(Arrays.equals(MockBlocks.CLEAR_DATABLOCK[0].getBlockData(), encrypted0));
		
		Decryptor decryptor = new Decryptor(secretKey, encryptor.getIV());
		byte[] clearText0 = decryptor.decrypt(encrypted0);
		byte[] clearText1 = decryptor.decrypt(encrypted1);
		assertTrue(Arrays.equals(MockBlocks.CLEAR_DATABLOCK[0].getBlockData(), clearText0));
		assertTrue(Arrays.equals(MockBlocks.CLEAR_DATABLOCK[1].getBlockData(), clearText1));
		
	}
	
	public void xtestCryptoSequence() throws Exception {
		Cipher encrpytionCypher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		encrpytionCypher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] clearText1 = "Une premiere ligne pour moi!".getBytes();
		byte[] clearText2 = "Plus de données font de beaux enfants!".getBytes();

		byte[] cipherText1 = encrpytionCypher.update(clearText1);
		byte[] cipherText2 = encrpytionCypher.doFinal(clearText2);

		Cipher decryptionCypher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParamSpec = new IvParameterSpec(encrpytionCypher.getIV());
		decryptionCypher.init(Cipher.DECRYPT_MODE, this.secretKey, ivParamSpec);
		byte[] recoveredText1 = decryptionCypher.update(cipherText1);
		byte[] recoveredText2 = decryptionCypher.doFinal(cipherText2);
		
		System.out.println(new String(recoveredText1));
		System.out.println(new String(recoveredText2));
		assertTrue(Arrays.equals(clearText1, recoveredText1));
		assertTrue(Arrays.equals(clearText2, recoveredText2));
	}

}

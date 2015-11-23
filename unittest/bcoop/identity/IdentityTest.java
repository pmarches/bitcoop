package bcoop.identity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.DSAParameterSpec;
import java.util.Hashtable;

import javax.crypto.SecretKey;

import mockObject.MockIdentityManager;

import junit.framework.TestCase;

public class IdentityTest extends TestCase {

	public void testHash(){
		Hashtable<Identity, String> hash = new Hashtable<Identity, String>();
		String toto1 = "toto1";
		String toto2 = "toto2";
		String toto3 = "toto3";

		Identity id1 = new Identity(0x1, toto1, (PublicKey) null);
		Identity id11 = new Identity(0x1, toto1, (PublicKey) null);
		Identity id2 = new Identity(0x2, toto2, (PublicKey) null);
		LocalIdentity id3 = new LocalIdentity(0x3, toto3, (PublicKey) null, (PrivateKey) null, (SecretKey) null);

		assertEquals(id1, id11);
		assertFalse(id1.equals(id2));
		assertFalse(id1.equals(id3));

		hash.put(id1, toto1);
		assertTrue(hash.containsKey(id1));
		assertTrue(hash.containsKey(id11));
		assertFalse(hash.containsKey(id2));
		assertFalse(hash.containsKey(id3));
		
		assertSame(hash.get(id1), toto1);
		assertSame(hash.get(id11), toto1);

		hash.put(id3, toto3);
		assertTrue(hash.containsKey(id3));
		assertSame(hash.get(id3), toto3);
	}
	
	public void testStore(){
		
	}

	/*
	 * Test method for 'bcoop.identity.Identity.writeExternal(ObjectOutput)'
	 */
	public void testWriteExternal() {
		try{
			MockIdentityManager mockIdentityManager = new MockIdentityManager(MockIdentityManager.CLIENT_IDENTITIES_PATH);
			Identity ident = mockIdentityManager.getLocalIdentity();
			
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outStream);
			oos.writeObject(ident);
			
			byte[] data = outStream.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			
			Identity identityRead = (Identity) ois.readObject();
			assertEquals(ident, identityRead);
			
			Identity ident2 = mockIdentityManager.getIdentity(mockIdentityManager.getUidArray()[0]);
			assertFalse(ident.equals(ident2));
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * This speeds up the key generation process. VERY Insecure! Only for unit tests!
	 * @return
	 */
	public static DSAParameterSpec getPrecomputedDSAParameters(){
		BigInteger g = new BigInteger("153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b410b7a0f12ca1cb9a428cc", 16);
		BigInteger p = new BigInteger("9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b", 16);
		BigInteger q = new BigInteger("9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b", 16);
		DSAParameterSpec params = new DSAParameterSpec(p, q, g);
		return params;
	}

}

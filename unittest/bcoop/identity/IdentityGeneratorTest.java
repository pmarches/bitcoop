package bcoop.identity;

import junit.framework.TestCase;

public class IdentityGeneratorTest extends TestCase {
	final static String HUMAN_READABLE_ALIAS = "identityStringAlias";
	
	public void testGenerateIdentity(){
		IdentityGenerator idGen = new IdentityGenerator(IdentityTest.getPrecomputedDSAParameters());
		LocalIdentity identity = idGen.generateLocalIdentity(HUMAN_READABLE_ALIAS);
		assertEquals(HUMAN_READABLE_ALIAS, identity.getHumanReadableAlias());
		assertTrue(0 != identity.getUniqueId());
		assertNotNull(identity.getUniqueId());
		assertNotNull(identity.getVerificationKey());
		assertNotNull(identity.getEncryptionKey());
	}
}

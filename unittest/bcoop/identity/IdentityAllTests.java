package bcoop.identity;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarchesseault
 *
 */

public class IdentityAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.identity");
		//$JUnit-BEGIN$
		suite.addTestSuite(IdentityTest.class);
		suite.addTestSuite(IdentityManagerTest.class);
		suite.addTestSuite(IdentityGeneratorTest.class);
		//$JUnit-END$
		return suite;
	}

}

package bcoop.util;

import junit.framework.Test;
import junit.framework.TestSuite;

public class UtilAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.util");
		//$JUnit-BEGIN$
		suite.addTestSuite(HashMatrixTest.class);
		suite.addTestSuite(UtilsTest.class);
		suite.addTestSuite(HashOfArrayTest.class);
		suite.addTestSuite(SuffixedNumberTest.class);
		suite.addTestSuite(ConfigurationTest.class);
		suite.addTestSuite(BitCoopFileTest.class);
		suite.addTestSuite(ObjectStoreTest.class);
		//$JUnit-END$
		return suite;
	}

}

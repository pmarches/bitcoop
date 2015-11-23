package bcoop.block;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BlockAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.block");
		//$JUnit-BEGIN$
		suite.addTestSuite(DataBlockTest.class);
		suite.addTestSuite(TransactionBlockTest.class);
		//$JUnit-END$
		return suite;
	}

}

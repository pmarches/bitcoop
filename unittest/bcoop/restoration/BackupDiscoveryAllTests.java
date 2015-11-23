package bcoop.restoration;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BackupDiscoveryAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.restoration");
		//$JUnit-BEGIN$
		suite.addTestSuite(BackupDiscoveryTest.class);
		//$JUnit-END$
		return suite;
	}

}

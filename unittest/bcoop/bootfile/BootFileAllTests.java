package bcoop.bootfile;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BootFileAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.bootfile");
		//$JUnit-BEGIN$
		suite.addTestSuite(BootFileTest.class);
		//$JUnit-END$
		return suite;
	}

}

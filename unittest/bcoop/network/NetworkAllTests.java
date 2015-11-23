/**
 * <p>Title: NetworkAllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class NetworkAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.network");
		//$JUnit-BEGIN$
        suite.addTestSuite(PeerManagerTest.class);
        suite.addTestSuite(PeerVisitorTest.class);
		//$JUnit-END$
		return suite;
	}
}

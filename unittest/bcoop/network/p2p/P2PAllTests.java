/**
 * <p>Title: P2PAllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class P2PAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.network.p2p");
		//$JUnit-BEGIN$
		suite.addTestSuite(P2PNetworkTest.class);
		suite.addTestSuite(P2PConnectionTest.class);
		//$JUnit-END$
		return suite;
	}
}

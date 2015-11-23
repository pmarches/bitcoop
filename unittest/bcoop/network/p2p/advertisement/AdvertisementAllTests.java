/**
 * <p>Title: AdvertisementAllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.advertisement;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class AdvertisementAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.network.p2p.advertisement");
		//$JUnit-BEGIN$
		suite.addTestSuite(AdvertisementManagerTest.class);
		suite.addTestSuite(AdvertisementManagerArchiverTest.class);
		suite.addTestSuite(PeerAdvertisementTest.class);
		//$JUnit-END$
		return suite;
	}
}

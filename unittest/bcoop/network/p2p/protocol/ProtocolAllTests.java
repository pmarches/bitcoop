/**
 * <p>Title: ProtocolAllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class ProtocolAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.network.p2p.protocol");
		//$JUnit-BEGIN$
        suite.addTestSuite(PipeStreamTest.class);
		suite.addTestSuite(PeerExchangeProtocolTest.class);
		suite.addTestSuite(BlockTransferProtocolTest.class);
        suite.addTestSuite(BlockRequestProtocolTest.class);
        suite.addTestSuite(ChallengeProtocolTest.class);
        suite.addTestSuite(AdjustAllowedSpaceProtocolTest.class);
		//$JUnit-END$
		return suite;
	}
}

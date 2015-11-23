/**
 * <p>Title: BlockTrackerAllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.blocktracker;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class BlockTrackerAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.blocktracker");
		//$JUnit-BEGIN$
		suite.addTestSuite(BlockTrackerTest.class);
		suite.addTestSuite(BlockTrackerArchiverTest.class);
		suite.addTestSuite(TransactionHistoryTest.class);
		//$JUnit-END$
		return suite;
	}
}

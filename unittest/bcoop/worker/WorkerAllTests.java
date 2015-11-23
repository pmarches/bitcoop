/**
 * <p>Title: AllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class WorkerAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.worker");
		//$JUnit-BEGIN$
		suite.addTestSuite(ClientWorkerTest.class);
		suite.addTestSuite(RestoreWorkerTest.class);
		suite.addTestSuite(BackupWorkerTest.class);
		suite.addTestSuite(BCoopServerTest.class);
		//$JUnit-END$
		return suite;
	}
}

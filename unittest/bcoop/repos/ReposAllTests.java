/**
 * <p>Title: ReposAllTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.repos;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class ReposAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.repos");
		//$JUnit-BEGIN$
		suite.addTestSuite(FSBlockRepositoryTest.class);
		suite.addTestSuite(FSRepositoryManagerTest.class);
		//$JUnit-END$
		return suite;
	}
}

/**
 * <p>Title: BackupTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup;

import bcoop.backup.blockfactory.BlockFactoryTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class BackupAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for bcoop.backup");
		//$JUnit-BEGIN$
		suite.addTestSuite(BlockFactoryTest.class);
        suite.addTestSuite(FileAssemblyTest.class);
        suite.addTestSuite(BackupPlanFactoryTest.class);
        suite.addTestSuite(DestinationChooserTest.class);
		//$JUnit-END$
		return suite;
	}
}

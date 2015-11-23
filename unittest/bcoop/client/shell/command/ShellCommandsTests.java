/**
 * <p>Title: ShellCommandsTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell.command;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class ShellCommandsTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for bcoop.shell.command");
        //$JUnit-BEGIN$
        suite.addTestSuite(RestoreCommandTest.class);
        suite.addTestSuite(ShowCommandTest.class);
        suite.addTestSuite(BackupCommandTest.class);
        suite.addTestSuite(DisconnectCommandTest.class);
        suite.addTestSuite(QuitCommandTest.class);
        //$JUnit-END$
        return suite;
    }
}

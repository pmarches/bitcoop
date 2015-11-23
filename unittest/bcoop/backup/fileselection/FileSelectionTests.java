/**
 * <p>Title: FileSelecltionTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.fileselection;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class FileSelectionTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for bcoop.backup.fileselection");
        //$JUnit-BEGIN$
        suite.addTestSuite(DataSelectorTest.class);
        //$JUnit-END$
        return suite;
    }
}

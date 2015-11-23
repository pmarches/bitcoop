/**
 * <p>Title: SchedulerTests.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author pmarches
 *
 */
public class SchedulerTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for bcoop.backup.scheduler");
        //$JUnit-BEGIN$
        suite.addTestSuite(JobFactoryTest.class);
        suite.addTestSuite(JobSchedulerTest.class);
        suite.addTestSuite(ScheduleTest.class);
        //$JUnit-END$
        return suite;
    }
}

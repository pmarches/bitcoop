/**
 * <p>Title: JobSchedulerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import java.util.Calendar;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class JobSchedulerTest extends TestCase {
    JobScheduler scheduler;
    ScheduledJob job;
    long jan_01_10_34_systime;
    
    public final void setUp() {
        scheduler = new JobScheduler();
        job = new ScheduledJob();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2005);
        cal.set(Calendar.MONTH, 01-1);
        cal.set(Calendar.DAY_OF_MONTH, 01);
        cal.set(Calendar.HOUR_OF_DAY, 10);
        cal.set(Calendar.MINUTE, 34);
        jan_01_10_34_systime = cal.getTimeInMillis();
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(34, cal.get(Calendar.MINUTE));
        assertEquals(2005, cal.get(Calendar.YEAR));

        Schedule jan01_10_34 = new Schedule("jan01_10_34");
        jan01_10_34.setMonth("01");
        jan01_10_34.setDay("01");
        jan01_10_34.setHour("10");
        jan01_10_34.setMinute("34");

        assertEquals(jan01_10_34.getMonth(), "01");
        assertEquals(jan01_10_34.getDay(), "01");
        assertEquals(jan01_10_34.getHour(), "10");
        assertEquals(jan01_10_34.getMinute(), "34");
        
        job.addSchedule(jan01_10_34);

        scheduler.addJob(job);
        
        assertEquals(1, scheduler.definedJobs.size());
    }

    public final void testGetNextScheduledJob() {
        scheduler.loadReadyJobs(1115169L);
        assertFalse(scheduler.hasJobReady());
        scheduler.loadReadyJobs(jan_01_10_34_systime);
        assertTrue(scheduler.hasJobReady());
        ScheduledJob job2 = scheduler.getNextScheduledJob(); 
        assertNotNull(job2);
        assertSame(job, job2);
        
    }

}

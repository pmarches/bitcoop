/**
 * <p>Title: ScheduleTest.java</p>
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
public class ScheduleTest extends TestCase {
    long jan_01_22_00;
    private long jul_12_17_14;
    private long jul_03_17_14;
    
    public void setUp(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2005);
        cal.set(Calendar.MONTH, 01-1);
        cal.set(Calendar.DAY_OF_MONTH, 01);
        cal.set(Calendar.HOUR_OF_DAY, 22);
        cal.set(Calendar.MINUTE, 00);
        jan_01_22_00 = cal.getTimeInMillis();

        cal.set(Calendar.MONTH, 07-1);
        cal.set(Calendar.DAY_OF_MONTH, 03);
        cal.set(Calendar.HOUR_OF_DAY, 17);
        cal.set(Calendar.MINUTE, 14);
        jul_03_17_14 = cal.getTimeInMillis();

        cal.set(Calendar.MONTH, 07-1);
        cal.set(Calendar.DAY_OF_MONTH, 12);
        jul_12_17_14 = cal.getTimeInMillis();


        cal.setTimeInMillis(jan_01_22_00);
        assertEquals(2005, cal.get(Calendar.YEAR));
        assertEquals(0, cal.get(Calendar.MONTH));
        assertEquals(01, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(22, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(00, cal.get(Calendar.MINUTE));
        
        
        cal.setTimeInMillis(jul_03_17_14);
        assertEquals(2005, cal.get(Calendar.YEAR));
        assertEquals(06, cal.get(Calendar.MONTH));
        assertEquals(03, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(17, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(14, cal.get(Calendar.MINUTE));

        cal.setTimeInMillis(jul_12_17_14);
        assertEquals(2005, cal.get(Calendar.YEAR));
        assertEquals(06, cal.get(Calendar.MONTH));
        assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(17, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(14, cal.get(Calendar.MINUTE));

    }

    public final void testIsScheduled() {
        Schedule everyHour = new Schedule("Every Minute");
        assertTrue(everyHour.isScheduled(0));
        assertTrue(everyHour.isScheduled(jan_01_22_00));
        assertTrue(everyHour.isScheduled(jul_12_17_14));

        Schedule everyMonthAt_12_17_14 = new Schedule("Every Month At");
        everyMonthAt_12_17_14.setDay("12");
        everyMonthAt_12_17_14.setHour("17");
        everyMonthAt_12_17_14.setMinute("14");
        assertFalse(everyMonthAt_12_17_14.isScheduled(jan_01_22_00));
        assertFalse(everyMonthAt_12_17_14.isScheduled(jul_03_17_14));
        assertTrue(everyMonthAt_12_17_14.isScheduled(jul_12_17_14));

        Schedule everySundayAt17_14 = new Schedule("Every Sunday");
        everySundayAt17_14.setWeekday("1");
        everySundayAt17_14.setHour("17");
        everySundayAt17_14.setMinute("14");
        assertFalse(everySundayAt17_14.isScheduled(jul_12_17_14));
        assertTrue(everySundayAt17_14.isScheduled(jul_03_17_14));

        Schedule onSpecificDate = new Schedule("Specific date");
        onSpecificDate.setMonth("07");
        onSpecificDate.setDay("03");
        onSpecificDate.setHour("17");
        onSpecificDate.setMinute("14");
        assertFalse(onSpecificDate.isScheduled(jul_12_17_14));
        assertTrue(onSpecificDate.isScheduled(jul_03_17_14));

    
        Schedule everyDayAt_17_14 = new Schedule("Every Day At");
        everyDayAt_17_14.setHour("17");
        everyDayAt_17_14.setMinute("14");
        assertFalse(everyDayAt_17_14.isScheduled(jan_01_22_00));
        assertTrue(everyDayAt_17_14.isScheduled(jul_03_17_14));
        assertTrue(everyDayAt_17_14.isScheduled(jul_12_17_14));
    }

}

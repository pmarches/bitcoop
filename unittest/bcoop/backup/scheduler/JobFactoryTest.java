/**
 * <p>Title: JobManagerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.util.BitCoopFile;
import bcoop.util.Configuration;
import bcoop.util.OSCapabilities;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class JobFactoryTest extends TestCase {
    private static final String PATTERN1 = "testData";
    private static final String PATTERN2 = "/SONIA/private/CVS/.*";
    private static final String PATTERN3 = ".*/*.iso";
    static final String validXmlData = 
"<bcoop>"+
"<backup>"+
"<fileset name=\"criticalFiles\">"+
"   <env name=\"soniaDbDump\" value=\"/tmp/soniaDb.sql\" />"+
"   <exec cmd=\"mysqldump sonia -usonia -Pdauphin >soniaDbDump\" />"+
"   <include pattern=\""+PATTERN1+"\" />"+
"   <include pattern=\""+PATTERN2+"\" />"+
"   <exclude pattern=\""+PATTERN3+"\" />"+
"   <exclude size=\"+100M\" />"+
"</fileset>"+
"<fileset name=\"noFiles\" />"+
"<schedule name=\"daily\">"+
"   <weekday value=\"*\" />"+
"   <day value=\"*\" />"+
"   <hour value=\"04\" />"+
"   <minute value=\"00\" />"+
"</schedule >"+
"<schedule name=\"bogus\">"+
"   <month value=\"01\" />"+
"   <weekday value=\"02\" />"+
"   <day value=\"03\" />"+
"   <hour value=\"04\" />"+
"   <minute value=\"05\" />"+
"</schedule >"+
"<job>"+
"   <schedule name=\"daily\" />"+
"   <schedule name=\"bogus\" />"+
"   <fileset name=\"criticalFiles\" />"+
"</job>"+
"</backup>"+
"</bcoop>";

    JobFactory jFactory;
    
    public void setUp() throws IOException{
        jFactory = new JobFactory(new Configuration(new ByteArrayInputStream(validXmlData.getBytes())));
    }
    
    public final void testChangeSchedule(){
    		//Ensure the objects are pointing among themselves, so that a change in the schedule will be reflected later
    		Vector<ScheduledJob> jobs = jFactory.getJobs();
    		ScheduledJob job = jobs.get(0);
    		assertSame(job.schedules.get(0), jFactory.scheduleList.get("daily"));
    		assertSame(job.schedules.get(1), jFactory.scheduleList.get("bogus"));
    		assertSame(job.namedDataGroup.get(0), jFactory.dataList.get("criticalFiles"));
    }
    
    public final void testLoadFromXML() {
    		try{
    	        assertNotNull(jFactory.scheduleList.get("bootime"));
    	        assertNotNull(jFactory.scheduleList.get("always"));
	        assertNotNull(jFactory.scheduleList.get("bootime"));
	        assertNotNull(jFactory.scheduleList.get("always"));
	        assertEquals(2, jFactory.dataList.size());
	        assertEquals(4, jFactory.scheduleList.size());
	        assertEquals(1, jFactory.getJobs().size());
	        
	        NamedDataGroup fPattern = jFactory.dataList.get("criticalFiles");
	        assertNotNull(fPattern);
	        assertEquals("criticalFiles", fPattern.getName());
	        String baseDirectory = new BitCoopFile(System.getProperty("user.dir")).getAbsolutePath();
	        assertEquals(baseDirectory+'/'+PATTERN1, fPattern.getDataSelector(0).getPattern());
	        if(OSCapabilities.isSingleRootFs()){
	        		assertEquals(PATTERN2, fPattern.getDataSelector(1).getPattern());
	        }
	        else{
        			assertEquals(baseDirectory+'/'+PATTERN2, fPattern.getDataSelector(1).getPattern());
	        }
	        assertEquals(PATTERN3, fPattern.getDataSelector(2).getPattern());
	        assertNull(fPattern.getDataSelector(3).getPattern());
	        
	        assertNotNull(jFactory.dataList.get("noFiles"));
	        assertNotNull(jFactory.scheduleList.get("daily"));
	        
	        ScheduledJob sJob = jFactory.getJobs().get(0);
	        assertSame(sJob.schedules.get(0), jFactory.scheduleList.get("daily"));
	        assertSame(sJob.namedDataGroup.get(0), jFactory.dataList.get("criticalFiles"));
	        
	        assertEquals(1, sJob.namedDataGroup.size());
	        Schedule daily = sJob.schedules.get(0);
	        assertNotNull(daily);
	        assertEquals("00", daily.getMinute());
	        assertEquals("04", daily.getHour());
	        assertEquals("*", daily.getDay());
	        assertEquals("*", daily.getWeekday());
	        assertEquals("*", daily.getMonth());
	
	        Schedule bogus = sJob.schedules.get(1);
	        assertNotNull(bogus);
	        assertEquals("05", bogus.getMinute());
	        assertEquals("04", bogus.getHour());
	        assertEquals("03", bogus.getDay());
	        assertEquals("02", bogus.getWeekday());
	        assertEquals("01", bogus.getMonth());
    		}
    		catch(Exception e){
    			e.printStackTrace();
    			fail();
    		}
}

}

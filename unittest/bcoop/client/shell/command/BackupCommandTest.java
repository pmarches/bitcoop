package bcoop.client.shell.command;

import java.util.Calendar;

import mockObject.MockClientConnection;
import junit.framework.TestCase;

public class BackupCommandTest extends TestCase {
	public void testBackup(){
		try{
			BackupCommand cmd = new BackupCommand(null);
			assertFalse(cmd.parse("allo"));
			
			MockClientConnection mockCon = new MockClientConnection();
			assertTrue(cmd.parse("backup importantStuff"));
			assertEquals("importantStuff", cmd.filesetToBackup);
			cmd.execute(mockCon);
			assertEquals(1, mockCon.backupCalled);
			assertEquals("importantStuff", mockCon.lastArguments.get(0));
	
			assertTrue(cmd.parse("backup funStuff at 20-12-2005 14:30"));
			assertEquals("funStuff", cmd.filesetToBackup);
			Calendar expectedDate = Calendar.getInstance();
			expectedDate.set(2005,12-1, 20, 14, 30, 00);
			expectedDate.set(Calendar.MILLISECOND, 0);
			
			assertEquals(expectedDate.getTime(), cmd.backtupAt);
			cmd.execute(mockCon);
			assertEquals(2, mockCon.backupCalled);
			assertEquals("funStuff", mockCon.lastArguments.get(0));
			assertEquals(expectedDate.getTime(), mockCon.lastArguments.get(1));
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
}

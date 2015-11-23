/**
 * <p>Title: RestoreCommandTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell.command;

import mockObject.MockClientConnection;
import bcoop.client.shell.command.RestoreCommand;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class RestoreCommandTest extends TestCase {
    public final void testRestoreCommand() {
    		try{
	        RestoreCommand cmd = new RestoreCommand(null);
	        assertFalse(cmd.parse("allo"));
	        assertFalse(cmd.parse("restore"));

	        assertTrue(cmd.parse("restore transaction 12312 to /tmpx/"));
	        assertTrue(cmd.parse("restore transaction 212"));

	        assertTrue(cmd.parse("restore transaction 43dRf4212  to /tmp/"));
	        assertEquals("/tmp/", cmd.destinationDir);
	        assertEquals("transaction", cmd.type);
	        assertEquals("43dRf4212", cmd.identifier);
	        MockClientConnection mockCon = new MockClientConnection();
	        cmd.execute(mockCon);
	        assertEquals(1, mockCon.restoreTransactionCalled);
	        assertEquals("43dRf4212", mockCon.lastArguments.get(0));
	
	        assertTrue(cmd.parse("restore file /myProject/abc.java"));
	        assertEquals(null, cmd.destinationDir);
	        assertEquals("file", cmd.type);
	        assertEquals("/myProject/abc.java", cmd.identifier);
	        cmd.execute(mockCon);
	        assertEquals(1, mockCon.restoreFileCalled);
	        assertEquals("/myProject/abc.java", mockCon.lastArguments.get(0));
	
	        assertTrue(cmd.parse("restore file /myProject/abc.java to   /tmp/    "));
	        assertEquals("/tmp/", cmd.destinationDir);
	        assertEquals("file", cmd.type);
	        assertEquals("/myProject/abc.java", cmd.identifier);
	        cmd.execute(mockCon);
	        assertEquals(2, mockCon.restoreFileCalled);
	        assertEquals("/myProject/abc.java", mockCon.lastArguments.get(0));
	    	
	        assertTrue(cmd.parse("restore transaction trans1"));
	        assertEquals(null, cmd.destinationDir);
	        assertEquals("transaction", cmd.type);
	        assertEquals("trans1", cmd.identifier);
	        cmd.execute(mockCon);
	        assertEquals("trans1", mockCon.lastArguments.get(0));
    		}
    		catch(Exception e){
    			e.printStackTrace();
    			fail();
    		}
    }

}

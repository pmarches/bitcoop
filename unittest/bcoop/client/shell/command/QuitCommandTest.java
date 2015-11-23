package bcoop.client.shell.command;

import mockObject.MockClientConnection;
import bcoop.client.shell.Shell;
import junit.framework.TestCase;

public class QuitCommandTest extends TestCase {
	public void testQuit(){
		try{
			Shell shell = new Shell();
			QuitCommand cmd = new QuitCommand(shell);
			assertFalse(cmd.parse("exedsef"));
			assertTrue(cmd.parse("quit"));
			cmd.execute(new MockClientConnection());
			assertFalse(shell.keepRunning);

			shell.keepRunning = true;
			assertTrue(cmd.parse("exit"));
			cmd.execute(new MockClientConnection());
			assertFalse(shell.keepRunning);
			
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
}

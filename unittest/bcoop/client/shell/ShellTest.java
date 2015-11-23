package bcoop.client.shell;

import bcoop.client.shell.command.Command;
import mockObject.MockClientConnection;
import junit.framework.TestCase;

public class ShellTest extends TestCase {

	/*
	 * Test method for 'bcoop.client.shell.Shell.parseLine(String)'
	 */
	public void testParseLine() {
		try{
			Shell shell = new Shell();
			MockClientConnection mClient = new MockClientConnection();
			Command command = shell.getCommandFromLine("disconnect");
			assertNotNull(command);
			command.execute(mClient);
			assertEquals(1, mClient.disconnectCalled);

			command = shell.getCommandFromLine("quit");
			assertNotNull(command);
			command.execute(mClient);
			assertEquals(2, mClient.disconnectCalled);
			assertFalse(shell.keepRunning);
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
}

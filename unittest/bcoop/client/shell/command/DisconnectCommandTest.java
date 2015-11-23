package bcoop.client.shell.command;

import mockObject.MockClientConnection;
import junit.framework.TestCase;

public class DisconnectCommandTest extends TestCase {
	public void testDisconnect(){
		try{
			DisconnectCommand cmd = new DisconnectCommand(null);
			MockClientConnection mockCon = new MockClientConnection();

			assertFalse(cmd.parse("asd"));

			assertTrue(cmd.parse("disconnect"));
			cmd.execute(mockCon);
			assertEquals(1, mockCon.disconnectCalled);
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
}

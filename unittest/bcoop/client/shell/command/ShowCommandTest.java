/**
 * <p>Title: FileStatusCommandTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell.command;

import bcoop.client.shell.command.ShowCommand;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class ShowCommandTest extends TestCase {

	public final void testShowCommandParsing() {
		ShowCommand cmd = new ShowCommand(null);
		assertFalse(cmd.parse("toto "));
		
		assertTrue(cmd.parse("show fileset"));
		assertEquals(ShowCommand.ShowWhat.FILESET, cmd.showWhat);

		assertTrue(cmd.parse("show file /tmp/toto.dat"));
		assertEquals(ShowCommand.ShowWhat.FILE, cmd.showWhat);
		assertEquals("/tmp/toto.dat", cmd.identifier);

		assertTrue(cmd.parse("show peer"));
		assertEquals(ShowCommand.ShowWhat.PEER, cmd.showWhat);

		assertTrue(cmd.parse("show schedule"));
		assertEquals(ShowCommand.ShowWhat.SCHEDULE, cmd.showWhat);
		
		assertTrue(cmd.parse("show repository"));
		assertEquals(ShowCommand.ShowWhat.REPOSITORY, cmd.showWhat);
		
		assertTrue(cmd.parse("show transaction"));
		assertEquals(ShowCommand.ShowWhat.TRANSACTION, cmd.showWhat);
		
		assertTrue(cmd.parse("show restore"));
		assertEquals(ShowCommand.ShowWhat.RESTORE, cmd.showWhat);
	}
}

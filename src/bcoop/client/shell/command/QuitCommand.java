/**
 * <p>Title: QuitCommand.java</p>
 * <p>Description: A commandto qui the CLI</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell.command;


import java.io.IOException;
import java.util.regex.Matcher;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;

/**
 * @author pmarches
 *
 */
public class QuitCommand extends Command{

    public QuitCommand(Shell shell) {
        super(shell);
    }

    public void execute(ClientConnection clientConnection) {
        this.ourShell.keepRunning = false;
        try {
			clientConnection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public String getPatternToMatch() {
        return "^("+getCmdPrefix()+"|exit)$";
    }

	@Override
	protected void initFromMatchedLine(Matcher matcher) {
	}

	@Override
	public String getCmdPrefix() {
		return "quit";
	}

	@Override
	public String getShortUsage() {
		return getCmdPrefix();
	}

	@Override
	public String getDetailedUsage() {
		return "Exits the shell";
	}
}

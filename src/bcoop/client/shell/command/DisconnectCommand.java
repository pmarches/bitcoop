package bcoop.client.shell.command;

import java.io.IOException;
import java.util.regex.Matcher;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;

public class DisconnectCommand extends Command{
	public DisconnectCommand(Shell ourShell) {
		super(ourShell);
	}

	@Override
	public void execute(ClientConnection clientConnection) throws IOException {
		clientConnection.disconnect();
	}

	@Override
	public String getPatternToMatch() {
		return "^"+getCmdPrefix()+"$";
	}

	@Override
	protected void initFromMatchedLine(Matcher matcher) {
	}

	@Override
	public String getCmdPrefix() {
		return "disconnect";
	}

	@Override
	public String getShortUsage() {
		return getCmdPrefix();
	}

	@Override
	public String getDetailedUsage() {
		return "Disconnects this client from the currently connected peer.";
	}

}

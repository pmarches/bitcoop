package bcoop.client.shell.command;

import java.io.IOException;
import java.util.regex.Matcher;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;

public class ConnectCommand extends Command{
	String ipAddress;
	
	public ConnectCommand(Shell ourShell) {
		super(ourShell);
	}

	@Override
	public void execute(ClientConnection clientConnection) throws IOException {
		clientConnection.disconnect();
		this.ourShell.disconnectFromHost();
		this.ourShell.connectToHost(ipAddress);
	}

	@Override
	public String getPatternToMatch() {
		return "^"+getCmdPrefix()+" +(\\S+)$";
	}

	@Override
	protected void initFromMatchedLine(Matcher matcher) {
		ipAddress = matcher.group(1);
	}

	@Override
	public String getCmdPrefix() {
		return "connect";
	}

	@Override
	public String getShortUsage() {
		return "connect <serverIp>";
	}

	@Override
	public String getDetailedUsage() {
		return "connects this shell to a bitcoop server";
	}

}

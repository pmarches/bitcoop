package bcoop.client.shell.command;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;

public class DeleteBlocksFromPeer extends Command {
	enum DeleteWhat{
		ALL,
		DATA,
		HEADER,
		TRANSACTION
	}
	DeleteWhat deleteWhat;
	String deleteFromWhatPeer;

	public DeleteBlocksFromPeer(Shell ourShell) {
		super(ourShell);
	}

	@Override
	public void execute(ClientConnection clientConnection) throws IOException {
		throw new NotImplementedException();
	}

	@Override
	public String getPatternToMatch() {
		return "^"+getCmdPrefix()+" (data|header|transaction|all) (\\S+)$";
	}

	@Override
	protected void initFromMatchedLine(Matcher matcher) throws ParseException {
		String deleteWhatStr = matcher.group(1);
		this.deleteWhat = DeleteWhat.valueOf(deleteWhatStr.toUpperCase().trim());
		this.deleteFromWhatPeer = matcher.group(2);
	}

	@Override
	public String getCmdPrefix() {
		return "delete";
	}

	@Override
	public String getShortUsage() {
		return getCmdPrefix()+" <data|header|transaction|all> [blockId]";
	}

	@Override
	public String getDetailedUsage() {
		return "Removes a specified block from any peer that has this block. Any dependant blocks are also deleted if they are not used by another block.";
	}

}

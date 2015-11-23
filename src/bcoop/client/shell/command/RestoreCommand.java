/**
 * <p>Title: RestoreCommand.java</p>
 * <p>Description: A command to start restoring a file</p>
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
public class RestoreCommand extends Command {
	public RestoreCommand(Shell ourShell) {
		super(ourShell);
	}

	String type;
	String identifier;
	String destinationDir;
	
	public String getPatternToMatch(){
		return "^"+getCmdPrefix()+" +(file|transaction) +(\\S+)( +to +(\\S+))?$";
	}
	
	public void execute(ClientConnection clientConnection) throws IOException {
		if(type.equals("file")){
			clientConnection.restoreFile(identifier, destinationDir);
		}
		else if(type.equals("transaction")){
			clientConnection.restoreTransaction(identifier, destinationDir);
		}
		else{
			throw new RuntimeException("Unkown type");
		}
	}

	@Override
	protected void initFromMatchedLine(Matcher matcher) {
		type = matcher.group(1);
		identifier = matcher.group(2);
		destinationDir = matcher.group(4);
	}

	@Override
	public String getCmdPrefix() {
		return "restore";
	}

	@Override
	public String getShortUsage() {
		return getCmdPrefix()+" <file pathName|transaction blockId> [ to <destinationDirectory>]";
	}

	@Override
	public String getDetailedUsage() {
		return "Starts a restoration job of the file specified by pathName. If transaction is used, then the transaction specified by the blockId is restored. The options destinationDirectory specifies where the restoration is done. By default, the restoration is done where the file was first backeduped.";
	}
	
}

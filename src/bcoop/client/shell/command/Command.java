/**
 * <p>Title: Command.java</p>
 * <p>Description: Basic command pattern for the CLI</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell.command;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;

/**
 * @author pmarches
 *
 */
public abstract class Command {
	Pattern cmdPattern;
	protected Shell ourShell;
	
	abstract public void execute(ClientConnection clientConnection) throws IOException;
	abstract public String getPatternToMatch();
	
	public Command(Shell ourShell){
		this.ourShell = ourShell;
	}
	
	public boolean parse(String line){
		if(line == null){
			return false;
		}
		line = line.trim();
		if(this.cmdPattern == null){
			this.cmdPattern = Pattern.compile(getPatternToMatch());
		}
		Matcher matcher = cmdPattern.matcher(line);
		
		if(matcher.matches()){
			try {
				initFromMatchedLine(matcher);
			} catch (ParseException e) {
				return false;
			}
			return true;
		}
		return false;
	}


	abstract public String getCmdPrefix();
	abstract protected void initFromMatchedLine(Matcher matcher) throws ParseException;
	abstract public String getShortUsage();
	abstract public String getDetailedUsage();
}

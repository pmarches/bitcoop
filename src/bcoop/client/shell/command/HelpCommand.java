package bcoop.client.shell.command;

import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;
import java.util.regex.Matcher;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;

public class HelpCommand extends Command {
	
	private String helpTopic;
	Vector<Command> commands;

	public HelpCommand(Shell shell, Vector<Command> commands) {
		super(shell);
		this.commands = commands;
	}

	@Override
	public void execute(ClientConnection clientConnection) throws IOException {
		if(helpTopic==null){
			for(Command cmd : commands){
				this.ourShell.out.println(cmd.getCmdPrefix()+"\t"+cmd.getShortUsage());
			}
		}
		else{
			for(Command cmd : commands){
				String cmdPrefix = cmd.getCmdPrefix().toUpperCase();
				if(cmdPrefix.startsWith(helpTopic.toUpperCase())){
					this.ourShell.out.println("Syntax: "+cmd.getShortUsage());
					this.ourShell.out.println(cmd.getDetailedUsage());
					break;
				}
			}
		}
	}

	@Override
	public String getPatternToMatch() {
		return "^"+getCmdPrefix()+"( (\\S+))?$";
	}

	@Override
	protected void initFromMatchedLine(Matcher matcher) throws ParseException {
		this.helpTopic = matcher.group(2);
	}

	@Override
	public String getCmdPrefix() {
		return "help";
	}

	@Override
	public String getShortUsage() {
		return "help [topic]";
	}

	@Override
	public String getDetailedUsage() {
		return "Use to get help about a command.";
	}
	

}

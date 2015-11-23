/**
 * <p>Title: Shell.java</p>
 * <p>Description: Main CLI class</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.command.Command;
import bcoop.client.shell.command.ConnectCommand;
import bcoop.client.shell.command.DeleteBlocksFromPeer;
import bcoop.client.shell.command.DisconnectCommand;
import bcoop.client.shell.command.HelpCommand;
import bcoop.client.shell.command.ShowCommand;
import bcoop.client.shell.command.QuitCommand;
import bcoop.client.shell.command.RestoreCommand;
import bcoop.client.shell.command.BackupCommand;

/**
 * @author pmarches
 *
 */
public class Shell{
	public boolean keepRunning = true;
	ClientConnection clientCon;
	Vector<Command> commands = new Vector<Command>();
	QuitCommand quitCommand;
	public PrintStream out;
	public PrintStream err;
	public InputStream in;
	ArrayList<String> history;
	private Command lastCommand;
	
	public Shell(){
		this.out = System.out;
		this.err = System.err;
		this.in = System.in;
		history = new ArrayList<String>();
		
		HelpCommand helpCmd = new HelpCommand(this, this.commands);
		commands.add(helpCmd);
		
		this.quitCommand = new QuitCommand(this);
		commands.add(quitCommand);
		commands.add(new RestoreCommand(this));
		commands.add(new ShowCommand(this));
		commands.add(new BackupCommand(this));
		commands.add(new ConnectCommand(this));
		commands.add(new DisconnectCommand(this));
		commands.add(new DeleteBlocksFromPeer(this));
	}
	
	public void run(String hostToConnect){
		try{
			connectToHost(hostToConnect);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.in));
			while(keepRunning){
				this.out.print("bcoop> ");
				this.out.flush();
				String line = reader.readLine();
				if(checkHistory(line)){
					
				}
				else{
					executeLine(line, true);
				}
			}
			disconnectFromHost();
		}
		catch(ConnectException ce){
			this.err.println("ERROR: Could not contact host "+ hostToConnect);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	private boolean checkHistory(String line) throws IOException {
		final Pattern historyPattern = Pattern.compile("^(h|history)( +(\\S+))?$");
		Matcher historyMatcher = historyPattern.matcher(line);
		if(historyMatcher.matches() == false){
			return false;
		}
		String executeHistory = historyMatcher.group(3);
		if(executeHistory==null){
			printHistory();
		}
		else{
			int whichHistory = Integer.parseInt(executeHistory);
			if(whichHistory>this.history.size() || whichHistory<=0){
				printHistory();
			}
			else{
				int index = this.history.size()-whichHistory;
				String commandWanted = this.history.get(index);
				this.out.println(commandWanted);
				executeLine(commandWanted, false);
			}
		}
		return true;
	}

	private void printHistory() {
		for(int i=0; i<this.history.size(); i++){
			int displayed = this.history.size()-i;
			this.out.printf("%-3d %s\n", displayed, history.get(i));
		}
	}

	public void executeLine(String line, boolean addToHistory) throws IOException {
		Command cmd = null;
		if(line == null){
			cmd = quitCommand;
		}
		else{
			cmd = getCommandFromLine(line);
			if(cmd==null){
				cmd = lastCommand;
				addToHistory = false; //Override
			}
		}
		if(cmd != null){
			boolean canExecute = clientCon.isConnected();
			if(canExecute==false){
				canExecute = cmd instanceof ConnectCommand;
			}
			if(canExecute){
				lastCommand = cmd;
				if(addToHistory){
					this.history.add(line);
				}
				cmd.execute(clientCon);
			}
			else{
				this.out.println("Must be connected");
			}
		}
		else{
			this.out.println("Command not found: "+ line);
		}
	}

	public void disconnectFromHost() throws IOException {
		clientCon.disconnect();
	}

	public void connectToHost(String hostToConnect) throws UnknownHostException, IOException {
		if(clientCon != null && clientCon.isConnected()){
			disconnectFromHost();
		}
		clientCon = new ClientConnection(hostToConnect);
	}
	
	protected Command getCommandFromLine(String line) {
		for(Command currentCommand : commands){
			if(currentCommand.parse(line)){
				return currentCommand;
			}
		}
		return null;
	}
	
	public static void main(String[] argv){
		String hostToConnect = "127.0.0.1";
		if(argv.length > 0){
			hostToConnect = argv[0];
		}
		Shell sh = new Shell();
		sh.run(hostToConnect);
	}
}

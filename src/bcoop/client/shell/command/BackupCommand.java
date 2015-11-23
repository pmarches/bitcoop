/**
 * <p>Title: ScheduleCommand.java</p>
 * <p>Description: A command that allows the creation of a new schedule</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell.command;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;

/**
 * @author pmarches
 *
 */
public class BackupCommand extends Command {
	private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
	String filesetToBackup;
	Date backtupAt;
	
	public BackupCommand(Shell ourShell) {
		super(ourShell);
	}
	
	public String getPatternToMatch() {
		return "^"+getCmdPrefix()+" +(\\S+)( +at +((\\p{Digit}|-|:| )+))?$";
	}

	public void execute(ClientConnection clientConnection) throws IOException {
		clientConnection.scheduleBackup(this.filesetToBackup, this.backtupAt);
	}

	@Override
	protected void initFromMatchedLine(Matcher matcher) throws ParseException {
		this.filesetToBackup = matcher.group(1);
		String timestamp = matcher.group(3);

		if(timestamp != null){
			SimpleDateFormat dateParser = new SimpleDateFormat();
			dateParser.applyPattern(DATE_FORMAT);
			backtupAt = dateParser.parse(timestamp);
			Calendar cal = Calendar.getInstance();
			cal.setTime(backtupAt);
			cal.set(Calendar.SECOND, 0);
			this.backtupAt=cal.getTime();
		}
	}

	@Override
	public String getCmdPrefix() {
		return "backup";
	}

	@Override
	public String getShortUsage() {
		return "backup <filesetname>[ at <"+DATE_FORMAT+">]";
	}

	@Override
	public String getDetailedUsage() {
		return "starts a backup at the specified time.";
	}
	
}

/**
 * <p>Title: ListHeaderBlockCommand.java</p>
 * <p>Description: A command to know the status of a file</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.client.shell.command;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Calendar;
import java.util.regex.Matcher;

import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.backup.scheduler.Schedule;
import bcoop.block.TransactionBlock;
import bcoop.client.api.ClientConnection;
import bcoop.client.shell.Shell;
import bcoop.identity.Identity;
import bcoop.util.SuffixedNumber;

/**
 * @author pmarches
 *
 */
public class ShowCommand extends Command {
	enum ShowWhat {
		FILESET, PEER, SCHEDULE, REPOSITORY, TRANSACTION, RESTORE, FILE
	}
	ShowWhat showWhat;
	String identifier;
	
    public ShowCommand(Shell ourShell) {
		super(ourShell);
	}

    public void execute(ClientConnection clientConnection) {
    		switch(showWhat){
	    		case PEER :
    				showPeer(clientConnection);
				break;
	    		case FILESET :
	    			showAllFileset(clientConnection);
    				break;
	    		case SCHEDULE :
	    			showAllSchedule(clientConnection);
    				break;
	    		case TRANSACTION:
	    			showLocalTransaction(clientConnection);
	    			break;
    		}
    }

	private void showPeer(ClientConnection clientConnection) {
    		try{
    			Vector<Identity> peerList = clientConnection.getAllPeer();
    			for(Identity peer : peerList){
					this.ourShell.out.println(peer.getHumanReadableAlias()+" (0x"+peer.getUniqueIdString()+")");
    			}
    		}
    		catch(IOException e){
    			e.printStackTrace();
    		}
    }

	/* Shows the transactions made by the host we control
     */
	private void showLocalTransaction(ClientConnection clientConnection) {
		try {
			final String formatString = "%-20s %-10s %-20s %-25s %-25s %-16s %s\n";
			Vector<TransactionBlock> peerTransaction = clientConnection.getAllTransaction();

			if(peerTransaction.isEmpty() == false){
				this.ourShell.out.printf(formatString, "DataName", "Version", "Schedule", "Transaction start", "Transaction end", "Size", "#Files");
			}
			for(TransactionBlock tBlock : peerTransaction){
				Calendar transactionDate = Calendar.getInstance();
				transactionDate.setTimeInMillis(tBlock.getTransactionStartTime());
				String hrDateStart = SimpleDateFormat.getDateTimeInstance().format(transactionDate.getTime());

				String hrDateEnd;
				if(tBlock.getTransactionEndTime() != 0){
					transactionDate.setTimeInMillis(tBlock.getTransactionEndTime());
					hrDateEnd = SimpleDateFormat.getDateTimeInstance().format(transactionDate.getTime());
				}
				else{
					hrDateEnd = "In progress";
				}

				String humanReadableSize = new SuffixedNumber(tBlock.getTransactionSize(), "bytes").toString();
				this.ourShell.out.printf(formatString,
						tBlock.getDataName(), Long.toHexString(tBlock.getBlockId()), tBlock.getScheduleName(), hrDateStart, hrDateEnd, humanReadableSize, tBlock.getNumberOfHeaderBlocks());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showAllFileset(ClientConnection clientConnection) {
		try {
			Vector<NamedDataGroup> allFileset = clientConnection.getAllDefinedFileset();
			for(NamedDataGroup fileset : allFileset){
				this.ourShell.out.println(fileset.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showAllSchedule(ClientConnection clientConnection) {
		try {
			Vector<Schedule> allSchedule = clientConnection.getAllDefinedSchedule();
			for(Schedule schedule : allSchedule){
				this.ourShell.out.println(schedule.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public String getPatternToMatch() {
        return "^"+getCmdPrefix()+"( +(fileset|peer|schedule|repository|transaction|restore|file))( +(\\S+))?$";
    }

	@Override
	protected void initFromMatchedLine(Matcher matcher) {
        String showTypeStr = matcher.group(2);
        this.showWhat = ShowWhat.valueOf(showTypeStr.trim().toUpperCase());
        identifier = matcher.group(4);
	}

	@Override
	public String getCmdPrefix() {
		return "show";
	}

	@Override
	public String getShortUsage() {
		return getCmdPrefix()+" <fileset|peer|schedule|repository|transaction|restore|file> [identifier]";
	}

	@Override
	public String getDetailedUsage() {
		return "The fileset shows the currently defined filesets.";
	}

}

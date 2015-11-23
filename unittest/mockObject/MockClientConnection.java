package mockObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.Date;

import bcoop.client.api.ClientConnection;

public class MockClientConnection extends ClientConnection {
	public int disconnectCalled;
	public int getListOfBackupJobs;
	public int backupCalled;
	public int restoreFileCalled;
	public int restoreTransactionCalled;
	public Vector<Object> lastArguments = new Vector<Object>();

	public MockClientConnection() throws UnknownHostException, IOException{
	}
	
	public void disconnect(){
		this.disconnectCalled++;
		lastArguments.clear();
	}
	
	public void con(){
	}
	
	public void restoreTransaction(String transactionName, String restoreToDirectory){
		this.restoreTransactionCalled++;
		lastArguments.clear();
		lastArguments.add(transactionName);
		lastArguments.add(restoreToDirectory);
	}
	
	public void restoreFile(String filepath, String restoreToDirectory){
		this.restoreFileCalled++;
		lastArguments.clear();
		lastArguments.add(filepath);
		lastArguments.add(restoreToDirectory);
	}
	
	public void scheduleBackup(String whatBackupJobName, Date whenToBackup) throws IOException{
		this.backupCalled++;
		lastArguments.clear();
		lastArguments.add(whatBackupJobName);
		lastArguments.add(whenToBackup);
	}
	
	public String[] getListOfBackupJobs(){
		this.getListOfBackupJobs++;
		lastArguments.clear();
		return null;
	}

}

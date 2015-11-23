package bcoop.client.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.Date;

import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.backup.scheduler.Schedule;
import bcoop.backup.scheduler.ScheduledJob;
import bcoop.block.TransactionBlock;
import bcoop.identity.Identity;

public class ClientConnection {
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	Socket clientSocket;
	
	public ClientConnection(String hostName) throws UnknownHostException, IOException{
        this.clientSocket = new Socket(hostName, ClientProtocol.DEFAULT_PORT);
        this.clientSocket.setKeepAlive(true);
        this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(clientSocket.getInputStream());
	}
	
	protected ClientConnection(){
		//For Mock object only
	}
	
	public void disconnect() throws IOException{
		if(this.clientSocket.isClosed()==false){
			ClientProtocol.c_disconnect(ois, oos);
			this.clientSocket.close();
		}
	}
	
	public void restoreTransaction(String transactionBlockId, String restoreToDirectory){
		try{
			ClientProtocol.c_restoreTransaction(transactionBlockId, restoreToDirectory, ois, oos);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void restoreFile(String filepath, String restoreToDirectory) throws IOException{
		ClientProtocol.c_restoreFile(filepath, restoreToDirectory, ois, oos);
	}
	
	public void scheduleBackup(String fileset, Date whenToBackup) throws IOException{
		ClientProtocol.c_backup(fileset, whenToBackup, ois, oos);
	}
	
	public Vector<NamedDataGroup> getAllDefinedFileset() throws IOException{
		return ClientProtocol.c_getDefinedFileset(ois, oos);
	}
	
	public Vector<ScheduledJob> getAllDefinedJobs() throws IOException{
		return ClientProtocol.c_getScheduledJobs(ois, oos);
	}
	
	public Vector<Schedule> getAllDefinedSchedule() throws IOException{
		return ClientProtocol.c_getDefinedSchedule(ois, oos);
	}
	
	public Vector<TransactionBlock> getAllTransaction() throws IOException {
		return ClientProtocol.c_getAllTransactionBlock(ois, oos);
	}

	public boolean isConnected() {
		return !this.clientSocket.isClosed();
	}

	public Vector<Identity> getAllPeer() throws IOException {
		return ClientProtocol.c_getallPeer(ois, oos);
	}
}

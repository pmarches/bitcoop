/**
 * <p>Title: ClientWorker.java</p>
 * <p>Description: Thread that processes commands from a GUI or CLI</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import bcoop.client.api.ClientProtocol;
import bcoop.server.BCoopServerBase;
import bcoop.util.Configuration;

/**
 * @author pmarches
 *
 */
public class ClientWorker extends ThreadWorker{
	private BCoopServerBase server;
	ServerSocket serverSocket;
	Socket clientSocket;
	
	public ClientWorker(Configuration config, BCoopServerBase server) throws UnknownHostException, IOException{
		super("ClientWorker");
		this.server = server;
		int port = Integer.parseInt(config.getProperty(Configuration.CLIENT_PORT, Integer.toString(ClientProtocol.DEFAULT_PORT)));
		serverSocket = new ServerSocket(port, 1);
	}
	
	synchronized public void execute(){
		try{
			Logger.getLogger(this.getClass()).debug("Waiting for a client to connect");      
			clientSocket = serverSocket.accept();
			Logger.getLogger(this.getClass()).info("Got a connection from ip: "+ clientSocket.getRemoteSocketAddress().toString());      
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
			
			//TODO Auth...
			while(true){
				if(ClientProtocol.doExecuteServerSide(this.server, ois, oos) == false){
					break;
				}
			}
			clientSocket.close();
		}
		catch(SocketException e){
			//Ok, we are being shutdown..
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void shutdown(){
		try{
			stopCallingExecute();
			serverSocket.close();
			if(clientSocket != null){
				clientSocket.close();
			}
			super.shutdown();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}

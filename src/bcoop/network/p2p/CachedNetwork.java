package bcoop.network.p2p;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Hashtable;

import bcoop.exception.ConnectionRefusedException;
import bcoop.identity.Identity;
import bcoop.network.Connection;
import bcoop.network.Network;

public class CachedNetwork {
	private Network network;
	private Hashtable<Identity, Connection> cachedConnection;
	
	public CachedNetwork(Network network){
		this.network = network;
		this.cachedConnection = new Hashtable<Identity, Connection>();
	}
	
	public synchronized Connection getCachedConnection(Identity peerId) throws UnknownHostException, IOException, ConnectionRefusedException{
		Connection connection = this.cachedConnection.get(peerId);
		if(connection == null){
			connection = this.network.getConnection(peerId);
			this.cachedConnection.put(peerId, connection);
		}
		return connection;
	}
	
	public synchronized void closeAllCachedConnection() throws IOException{
		for(Connection con : this.cachedConnection.values()){
			con.close();
		}
		this.cachedConnection.clear();
	}
}

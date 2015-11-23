/**
 * <p>Title: P2PNetwork.java</p>
 * <p>Description: A TCPIP implementation of the network interface. Allows the access to other peers</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.Iterator;

import org.apache.log4j.Logger;

import bcoop.exception.ConnectionRefusedException;
import bcoop.exception.MissingConfigurationException;
import bcoop.identity.Identity;
import bcoop.identity.LocalIdentity;
import bcoop.network.Connection;
import bcoop.network.Network;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.network.p2p.advertisement.AdvertisementManagerArchiver;
import bcoop.network.p2p.advertisement.DiscoveryService;
import bcoop.network.p2p.advertisement.PeerAdvertisement;
import bcoop.network.p2p.protocol.ProtocolFactory;
import bcoop.util.Configuration;

/**
 * @author pmarches
 *
 */
public class P2PNetwork extends Network{
	public static final int SERVER_PORT = 9701;
	public static final String ADV_MANAGER_FILENAME="advManager.dat";
	
	ProtocolFactory protFactory = null;

	private int serverPort;
	private P2PServer serverThread;
	private DiscoveryService dService;

	private Configuration config;
	private LocalIdentity ourPeerId;

	public P2PNetwork(LocalIdentity ourPeerId, Configuration config) throws NumberFormatException, MissingConfigurationException{
		this.config = config;
		this.serverPort = Integer.parseInt(config.getProperty(Configuration.OUR_SERVER_PORT));
		this.ourPeerId = ourPeerId;

        try{
			String filePath = AdvertisementManagerArchiver.getAdvertisementFilePath(config);
			FileInputStream fis = new FileInputStream(filePath);
			this.peerManager = AdvertisementManagerArchiver.loadFrom(fis);
			Logger.getLogger(getClass()).info("Loaded AdvertisementManager from file "+filePath+" with "+this.peerManager.getNumberOfRemotePeers()+" peers.");
		}
		catch(Exception e){
			Logger.getLogger(getClass()).info("Loading default (empty) AdvertisementManager");
	        this.peerManager = new AdvertisementManager(AdvertisementManager.createPeerAdvertisement(this.ourPeerId, serverPort, AdvertisementManager.EXPIRATION_DELTA));
		}
		this.serverThread = new P2PServer();
	}
	
	class P2PServer extends Thread{
		ServerSocket serverSocket;
        Vector<P2PWorker> workers = new Vector<P2PWorker>();
		private boolean isAcceptingConnections; 
		
		P2PServer(){
			super("P2PNetworkAcceptThread");
		}
		
		public void run(){
			try{
				serverSocket = new ServerSocket(serverPort);
				synchronized(this){
					notifyAll();
				}
				while(serverSocket.isClosed()==false){
                     removeInactiveWorkers();
					Socket clientSocket = null;
					try{
						this.isAcceptingConnections = true;
						clientSocket = serverSocket.accept();
						Logger.getLogger(getClass()).debug("server got a connection");
					}
					catch(SocketException e){
						if(this.isAcceptingConnections){
							//Abnormal termination
							Logger.getLogger(this.getClass()).error("Abnormal termination of server socket");
							e.printStackTrace();
						}
						Logger.getLogger(this.getClass()).info("Closing P2P server socket");
						return;
					}
					P2PWorker worker = new P2PWorker(clientSocket, repositoryManager, (AdvertisementManager) peerManager, blockHandler);
                    registerWorker(worker);                    
					worker.start();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private void registerWorker(P2PWorker worker) {
            synchronized(workers){
                workers.add(worker);
            }
        }
        
        private void unregisterWorker(P2PWorker worker) {
            synchronized(workers){
                workers.remove(worker);
            }
        }

        private void removeInactiveWorkers() {
        		synchronized(workers){
				Iterator it = workers.iterator();
				while(it.hasNext()){
				    P2PWorker worker = (P2PWorker) it.next();
				    if(!worker.isAlive()) it.remove();
				}
        		}
        }

        public void shutdown() throws IOException, InterruptedException{
        	this.isAcceptingConnections = false;
			serverSocket.close();
             join();
            
	    		synchronized(workers){
	            for(P2PWorker worker: workers){
	                worker.shutdown();
	            }
	    		}
		}
		
		public void start(){
			super.start();
			waitUntilReady();
		}

		/**
		 * This method blocks until  
		 */
		synchronized public void waitUntilReady() {
			try {
				while(serverSocket == null){
					wait(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public boolean isAcceptingConnections() {
			return isAcceptingConnections;
		}
		
	}
	
	public void bootNetwork() throws IOException{
        if(serverThread.isAlive()){
            return;
        }
		serverThread.start();

		String bootPeer = config.getProperty(Configuration.BOOT_SERVER_IP, null);
		int bootPort = Integer.parseInt(config.getProperty(Configuration.BOOT_SERVER_PORT, Integer.toString(SERVER_PORT)));

		if(this.blockHandler == null){
			throw new RuntimeException("BlockReceiverHandler must be set before calling boot()");
		}
		if(this.peerManager == null){
			throw new RuntimeException("PeerManager must be set before calling boot()");
		}
        if(this.repositoryManager == null){
            throw new RuntimeException("repositoryManager must be set before calling boot()");
        }
		dService = new DiscoveryService(bootPeer, bootPort, ourPeerId, (AdvertisementManager) this.peerManager);

		dService.start();
	}

	/* (non-Javadoc)
	 * @see bcoop.network.Network#shutdownNetwork()
	 */
	public void shutdownNetwork() {
		try{
			serverThread.shutdown();
			dService.shutdown();

			String fullPath = AdvertisementManagerArchiver.getAdvertisementFilePath(config);
			Logger.getLogger(this.getClass()).debug("Saving advertisements to file "+fullPath);
			AdvertisementManagerArchiver.saveTo((AdvertisementManager) this.peerManager, new FileOutputStream(fullPath));
		}
		catch(MissingConfigurationException e){
			Logger.getLogger(this.getClass()).debug("We are not saving advertisement because no filename is configured for this.");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see bcoop.network.Network#getConnection(java.lang.String)
	 */
	public Connection getConnection(Identity remotePeerId) throws IOException, ConnectionRefusedException{
		PeerAdvertisement peerAd = (PeerAdvertisement) ((AdvertisementManager) peerManager).getPeerAd(remotePeerId);
		if(peerAd == null){
			throw new UnknownHostException("Network knows nothing about "+remotePeerId);
		}
        return new P2PConnection(this.ourPeerId, peerAd.getIpAddress(), peerAd.getPort());
	}
	
	public boolean isAcceptingConnections(){
		if(serverThread==null){
			return false;
		}
		return serverThread.isAcceptingConnections();
	}
}

/**
 * <p>Title: P2PWorker.java</p>
 * <p>Description: A thread that services request made by other peers in client mode.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;

import bcoop.identity.Identity;
import bcoop.network.BlockReceiverHandler;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.network.p2p.protocol.Protocol;
import bcoop.network.p2p.protocol.ProtocolFactory;
import bcoop.repos.BlockRepository;
import bcoop.repos.RepositoryManager;
import bcoop.worker.ThreadWorker;

/**
 * This worker is server-side 
 * @author pmarches
 *
 */
public class P2PWorker extends ThreadWorker {
	Socket socket;
	private ProtocolFactory protFactory;
    Identity remotePeerId;
	
	public P2PWorker(Socket socket, RepositoryManager repositoryManager, AdvertisementManager peerManager, BlockReceiverHandler blockHandler) throws IOException{
		super("P2PWorker servicing ip: "+socket.getRemoteSocketAddress().toString());
		this.socket = socket;
        
        try{
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            remotePeerId = (Identity) ois.readObject();
            if(remotePeerId == null){
            	throw new IOException("Received a null remotePeerId");
            }
            Logger.getLogger(this.getClass()).debug(peerManager.getLocalPeerAdvertisement().getPeerId()+" received new P2P connection from "+remotePeerId);
            BlockRepository repos = repositoryManager.getRepositoryForPeer(remotePeerId);
            protFactory = new ProtocolFactory(repos, peerManager, blockHandler);
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
	}
    
	public void execute(){
		if(this.socket.isClosed()){
            stopCallingExecute();
            return;
        }
		try{
			Protocol prot = protFactory.getServerProtocolInstance(remotePeerId, this.socket);
			if(prot == null){
                Logger.getLogger(this.getClass()).debug("Read End of communication");
                stopCallingExecute();
				return;
			}
			prot.executeServer();
		}
        catch(SocketException e){
            //We are being shut down..
        }
		catch(Exception e){
			e.printStackTrace();
		}
	}

    public void shutdown(){
        try{
            socket.close();
            interrupt();
            super.shutdown();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}


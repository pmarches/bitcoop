/**
 * <p>Title: Network.java</p>
 * <p>Description: Base interface to create a network of peers</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

import java.io.IOException;
import java.net.UnknownHostException;

import bcoop.exception.ConnectionRefusedException;
import bcoop.identity.Identity;
import bcoop.repos.RepositoryManager;

/**
 * @author pmarches
 *
 */
public abstract class Network {
	protected PeerManager peerManager = null;
    protected BlockReceiverHandler blockHandler = null;
    protected RepositoryManager repositoryManager = null;

	abstract public Connection getConnection(Identity peerId) throws UnknownHostException, IOException, ConnectionRefusedException;
	
	public abstract void bootNetwork() throws IOException;
	public abstract void shutdownNetwork();
	
	public void setBlockReceiverHandler(BlockReceiverHandler blockHandler){
		this.blockHandler = blockHandler;
	}
	public BlockReceiverHandler getBlockReceiverHandler(){
		return this.blockHandler;
	}

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }
    public void setRepositoryManager(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }
    
    public PeerManager getPeerManager() {
        return peerManager;
    }
    public void setPeerManager(PeerManager peerManager) {
        this.peerManager = peerManager;
    }
}

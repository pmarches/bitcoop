/**
 * <p>Title: DiscoveryService.java</p>
 * <p>Description: A thread that does propagation of advertisements</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.advertisement;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import bcoop.exception.ConnectionRefusedException;
import bcoop.exception.StreamClosedException;
import bcoop.identity.LocalIdentity;
import bcoop.network.Connection;
import bcoop.network.p2p.P2PConnection;
import bcoop.worker.ThreadWorker;

/**
 * @author pmarches
 *
 */
public class DiscoveryService extends ThreadWorker{
	private AdvertisementManager peerManager;
    private LocalIdentity localPeerId;

	public DiscoveryService(String bootIp, int bootPort, LocalIdentity localPeerId, AdvertisementManager peerManager) throws IOException{
		super("DiscoveryServiceThread");
		this.peerManager = peerManager;
        this.localPeerId = localPeerId;

		if(bootIp != null){
			Logger.getLogger(this.getClass()).debug(localPeerId+" Want to exchange info with "+bootIp);
			try {
				exchangePeerInformationWith(bootIp, bootPort);
			} catch (UnknownHostException e) {
				Logger.getLogger(this.getClass()).error("Unkown host, cannot boot to host "+bootIp+":"+bootPort);
			} catch (ConnectionRefusedException e) {
				Logger.getLogger(this.getClass()).error("Connection was refused to host "+bootIp+":"+bootPort);
			}
		}
	}

	/**
	 * @param bootIp
	 * @param bootPort
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void exchangePeerInformationWith(String bootIp, int bootPort) throws UnknownHostException, ConnectionRefusedException{
		try{
            Connection con = new P2PConnection(localPeerId, bootIp, bootPort);
            con.exchangePeer(peerManager);
            con.close();
		}
		catch(StreamClosedException e){
			//Can't do anything about this..
		}
		catch(IOException e){
			e.printStackTrace();
			//Ignore connection failures.
		}
	}

	synchronized public void execute(){
		PeerAdvertisement peerAd=null;
		try{
			peerAd = (PeerAdvertisement) peerManager.getRandomPeerAdvertisement();
			if(peerAd != null){
				exchangePeerInformationWith(peerAd.getIpAddress(), peerAd.getPort());
			}
			else{
				Logger.getLogger(this.getClass()).info("We know no peer. Waiting for some peer to connect to us...");
			}
			wait(60*1000);
		}
		catch(ConnectionRefusedException cre){
			Logger.getLogger(this.getClass()).error(peerAd.getPeerId()+" refused connection. Removing it from the peer manager");
			this.peerManager.removePeer(peerAd.peerId);
		}
        catch(InterruptedException e){
            //Ok, we re being shutdown..
        }
		catch(Exception e){
			Logger.getLogger(this.getClass()).debug("Could not exchange peer information with "+peerAd.peerId);
			e.printStackTrace();
		}
	}
}

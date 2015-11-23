/**
 * <p>Title: PeerExchangeProtocol.java</p>
 * <p>Description: Protocol that allows the propagation of peer advertisement to other peers.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bcoop.exception.StreamClosedException;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.network.p2p.advertisement.PeerAdvertisement;

/**
 * @author pmarches
 *
 */
public class PeerExchangeProtocol extends Protocol{

	private AdvertisementManager peerManager;
	private String ipAddressOfRemoteEnd;

	/**
	 * @param observer
	 * @param iStream
	 * @param oStream
	 * @throws IOException
	 */
	public PeerExchangeProtocol(AdvertisementManager peerManager, String ipAddressOfRemoteEnd, InputStream iStream, OutputStream oStream) throws IOException {
		super(Protocol.PEER_EXCHANGE, iStream, oStream);
		this.peerManager = peerManager;
		this.ipAddressOfRemoteEnd = ipAddressOfRemoteEnd;
	}
	
	/* (non-Javadoc)
	 * @see bcoop.network.protocol.Protocol#executeProtocol()
	 */
	protected void executeSendReceive() throws IOException {
		PeerAdvertisement ourPeers[];
		if(this.peerManager != null){
			ourPeers = this.peerManager.getPeerAdvertisementArray();
		}
		else{
			ourPeers = new PeerAdvertisement[0]; 
		}
		ooStream.writeObject(ourPeers);
		ooStream.flush();
		
		try {
			PeerAdvertisement newPeers[] = (PeerAdvertisement[]) oiStream.readObject();
			if(newPeers.length >=1){//Must set the ip of the remote end here because of firewall ip is != than peer ip!
				newPeers[0].setIpAddress(ipAddressOfRemoteEnd);
			}
			peerManager.addAdvertisement(newPeers);
		} catch (ClassNotFoundException e) {
			throw new IOException("Class not found exception "+e);
		}
	}

	/* (non-Javadoc)
	 * @see bcoop.network.p2p.protocol.Protocol#executeClient()
	 */
	public CommandResult executeClient() throws IOException, StreamClosedException {
		initProtocol();
		executeSendReceive();
		return CommandResult.OK_RESULT;
	}

	/* (non-Javadoc)
	 * @see bcoop.network.p2p.protocol.Protocol#executeServer()
	 */
	public void executeServer() throws IOException, StreamClosedException {
		initProtocol();
		executeSendReceive();		
	}

}

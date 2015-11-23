/**
 * <p>Title: ProtocolFactory.java</p>
 * <p>Description: Runs on the server side to determine what the client wants us to do</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

import bcoop.identity.Identity;
import bcoop.network.BlockReceiverHandler;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.repos.BlockRepository;

/**
 * @author pmarches
 *
 */
public class ProtocolFactory {
	private AdvertisementManager peerManager;
	private BlockReceiverHandler blockReceiverHandler;
    private BlockRepository blockRepository;
	
	public ProtocolFactory(BlockRepository blockRepository, AdvertisementManager peerManager, BlockReceiverHandler blockReceiverHandler){
        this.blockRepository = blockRepository;
		this.blockReceiverHandler = blockReceiverHandler;
		this.peerManager = peerManager;
	}
	
	/**
	 * Called by server, receiving the connection.. 
	 * @param socket
	 * @return
	 * @throws IOException 
	 * @throws IOException 
	 */
	public Protocol getServerProtocolInstance(Identity remotePeerId, Socket socket) throws IOException{
		Protocol prot = null;
		InputStream in = socket.getInputStream();
		byte operation = (byte) in.read();
		switch(operation){
		case -1:
			return null;

		case Protocol.TRANSFERT_BLOCK:
			prot = new BlockTransferProtocol(blockReceiverHandler, remotePeerId, socket.getInputStream(), socket.getOutputStream());
			break;
			
		case Protocol.PEER_EXCHANGE:
			prot = new PeerExchangeProtocol(this.peerManager, socket.getInetAddress().getHostAddress(), socket.getInputStream(), socket.getOutputStream());
			break;
			
		case Protocol.CLOSE_CONNECTION:
			prot = new CloseProtocol(socket.getInputStream(), socket.getOutputStream());
			break;

        case Protocol.REQUEST_BLOCK:
            prot = new BlockRequestProtocol(blockRepository, socket.getInputStream(), socket.getOutputStream());
            break;

        case Protocol.VERIFY_BLOCK:
            prot = new ChallengeProtocol(blockRepository, socket.getInputStream(), socket.getOutputStream());
            break;
            
        case Protocol.REQUEST_HEADER_BLOCK:
            prot = new HeaderBlockRequestProtocol(blockRepository, socket.getInputStream(), socket.getOutputStream());
            break;

        case Protocol.INVALIDATE_BLOCK:
            prot = new InvalidateBlockProtocol(blockRepository, socket.getInputStream(), socket.getOutputStream());
            break;

        case Protocol.ADJUST_SPACE:
            prot = new AdjustAllowedSpaceProtocol(blockRepository, socket.getInputStream(), socket.getOutputStream());
            break;
            
        default:
            Logger.getLogger(this.getClass()).error("Got an unknown command:"+operation);
            break;
		}
		prot.setReadRemoteType(false);
		return prot;
	}

}

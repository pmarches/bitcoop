/**
 * <p>Title: P2PConnection.java</p>
 * <p>Description: A connection to another peer.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import org.apache.log4j.Logger;

import bcoop.block.HeaderBlock;
import bcoop.block.NumberedBlock;
import bcoop.exception.BlockRefusedException;
import bcoop.exception.ConnectionRefusedException;
import bcoop.exception.StreamClosedException;
import bcoop.identity.LocalIdentity;
import bcoop.network.Challenge;
import bcoop.network.Connection;
import bcoop.network.PeerManager;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.network.p2p.protocol.AdjustAllowedSpaceProtocol;
import bcoop.network.p2p.protocol.BlockRequestProtocol;
import bcoop.network.p2p.protocol.BlockTransferProtocol;
import bcoop.network.p2p.protocol.ChallengeProtocol;
import bcoop.network.p2p.protocol.CloseProtocol;
import bcoop.network.p2p.protocol.CommandResult;
import bcoop.network.p2p.protocol.HeaderBlockRequestProtocol;
import bcoop.network.p2p.protocol.InvalidateBlockProtocol;
import bcoop.network.p2p.protocol.PeerExchangeProtocol;

/**
 * @author pmarches
 *
 */
public class P2PConnection implements Connection {
	Socket socket;
	
	/**
	 * @param string
	 * @param i
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws ConnectionRefusedException 
	 */
	public P2PConnection(LocalIdentity localPeerId, String ip, int port) throws UnknownHostException, IOException, ConnectionRefusedException {
		try{
			socket = new Socket(ip, port);
			Logger.getLogger(getClass()).debug("Client got conenction to server");
	
	        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); 
	        oos.writeObject(localPeerId.getBaseIdentity()); //Carefull!! Must not send the LocalIdentity, it contains the private key!
		}
		catch(ConnectException ce){
			throw new ConnectionRefusedException();
		}
	}
	
	public void assertConnectionOpen(){
		if(socket.isClosed()) throw new RuntimeException("Connection is closed.");
		
	}

	/* (non-Javadoc)
	 * @see bcoop.network.Connection#sendBlock(bcoop.block.NumberedBlock)
	 */
	public long sendBlock(NumberedBlock blockToSend) throws BlockRefusedException, IOException, StreamClosedException{
		assertConnectionOpen();
		BlockTransferProtocol bTransfer = new BlockTransferProtocol(blockToSend, socket.getInputStream(), socket.getOutputStream());
		bTransfer.setReadRemoteType(true);
		CommandResult result = bTransfer.executeClient();
		if(result.hasErrorOccured()){
			throw new BlockRefusedException(result.getErrorMsg());
		}
		return result.getLong();
	}

	/* (non-Javadoc)
	 * @see bcoop.network.Connection#sendPeer()
	 */
	public void exchangePeer(PeerManager clientPeerManager) throws IOException, StreamClosedException {
		assertConnectionOpen();
		PeerExchangeProtocol peerProt = new PeerExchangeProtocol((AdvertisementManager) clientPeerManager, socket.getInetAddress().getHostAddress(), socket.getInputStream(), socket.getOutputStream() );
		peerProt.executeClient();
	}

	/* (non-Javadoc)
	 * @see bcoop.network.Connection#challenge()
	 */
	public void challenge(Challenge challenge) throws IOException, StreamClosedException {
		assertConnectionOpen();

        ChallengeProtocol challengeProt = new ChallengeProtocol(challenge, socket.getInputStream(), socket.getOutputStream());
        challengeProt.executeClient();
	}

	/* (non-Javadoc)
	 * @see bcoop.network.Connection#requestBlock()
	 */
	public NumberedBlock requestBlock(long desiredBlockId) throws IOException, StreamClosedException {
		assertConnectionOpen();

        BlockRequestProtocol requestProt = new BlockRequestProtocol(desiredBlockId, socket.getInputStream(), socket.getOutputStream());
        requestProt.executeClient();
        return requestProt.getReceivedBlock();
	}

	/* (non-Javadoc)
	 * @see bcoop.network.Connection#close()
	 */
	public void close() throws IOException {
		if(socket.isClosed()) return;
		CloseProtocol closeProt = new CloseProtocol(socket.getInputStream(), socket.getOutputStream());
		try {
			closeProt.executeClient();
		}
		catch (StreamClosedException e) {
			//Ignore dirty closes
		}
		finally{
			socket.close();
		}
	}

    public Vector<HeaderBlock> requestAllHeaderBlock() throws IOException, StreamClosedException {
        assertConnectionOpen();

        HeaderBlockRequestProtocol requestProt = new HeaderBlockRequestProtocol(socket.getInputStream(), socket.getOutputStream());
        requestProt.executeClient();
        Vector<Long> headerIds = requestProt.getReceivedHeaderBlock();
        Vector<HeaderBlock> headerBlocks = new Vector<HeaderBlock>(); 
        for(Long hBlockId : headerIds){
            //FIXME We should not create a new prot every time...
            BlockRequestProtocol brProt = new BlockRequestProtocol(hBlockId, socket.getInputStream(), socket.getOutputStream());
            brProt.executeClient();
            headerBlocks.add((HeaderBlock) brProt.getReceivedBlock());
        }
        return headerBlocks;
    }

    public void invalidateBlockId(Long blockId) throws IOException, StreamClosedException {
        assertConnectionOpen();
        
        InvalidateBlockProtocol invalidateProt = new InvalidateBlockProtocol(blockId, socket.getInputStream(), socket.getOutputStream());
        invalidateProt.executeClient();
    }

	public void offerLocalFreeSpace(long localSpaceForPeer) throws IOException {
        assertConnectionOpen();
        
        AdjustAllowedSpaceProtocol adjustProt = new AdjustAllowedSpaceProtocol(localSpaceForPeer, socket.getInputStream(), socket.getOutputStream());
		adjustProt.executeClient();
	}
}

/**
 * <p>Title: BlockTransferProtocol.java</p>
 * <p>Description: The protocol that allows the tranfert of blocks</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import bcoop.block.NumberedBlock;
import bcoop.exception.StorageLimitException;
import bcoop.exception.StreamClosedException;
import bcoop.identity.Identity;
import bcoop.network.BlockReceiverHandler;

/**
 * @author pmarches
 *
 */
public class BlockTransferProtocol extends Protocol {
	private BlockReceiverHandler receiverHandler;
	private NumberedBlock blockToSend;
    private Identity remotePeerId;

	public BlockTransferProtocol(BlockReceiverHandler receiverHandler, Identity remotePeerId, InputStream iStream, OutputStream oStream) throws IOException {
		super(Protocol.TRANSFERT_BLOCK, iStream, oStream);
		this.receiverHandler = receiverHandler;
        this.remotePeerId = remotePeerId;
	}

	public BlockTransferProtocol(NumberedBlock blockToSend, InputStream iStream, OutputStream oStream) throws IOException {
		super(Protocol.TRANSFERT_BLOCK, iStream, oStream);
		this.blockToSend = blockToSend;
	}

	/* (non-Javadoc)
	 * @see bcoop.network.p2p.protocol.Protocol#executeClient()
	 */
	public CommandResult executeClient() throws IOException, StreamClosedException {
		initProtocol();

		if(blockToSend == null){
			return CommandResult.OK_RESULT;
		}
		
		ooStream.writeObject(blockToSend);
		ooStream.flush();
		return getResult();
	}

	/* (non-Javadoc)
	 * @see bcoop.network.p2p.protocol.Protocol#executeServer()
	 */
	public void executeServer() throws IOException, StreamClosedException {
        CommandResult result = CommandResult.OK_RESULT;;
		initProtocol();

		try {
            NumberedBlock newBlock = (NumberedBlock) oiStream.readObject();
            long totalBytes = receiverHandler.handleReceivedBlock(remotePeerId, newBlock);
            result = new CommandResult();
            result.setLong(totalBytes);
		} catch (ClassNotFoundException e) {
			throw new IOException("Did not receive a block object :"+e.toString());
		} catch (StorageLimitException e) {
			Logger.getLogger(this.getClass()).debug(e.getMessage());
			result = new CommandResult(1, "failed to store block "+e.getMessage());
		}
        ooStream.writeObject(result);
        ooStream.flush();
	}

}

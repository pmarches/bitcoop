/**
 * <p>Title: Connection.java</p>
 * <p>Description: Base interface to create a connection to another peer</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

import java.io.IOException;
import java.util.Vector;

import bcoop.block.HeaderBlock;
import bcoop.block.NumberedBlock;
import bcoop.exception.BlockRefusedException;
import bcoop.exception.StreamClosedException;

/**
 * @author pmarches
 *
 */
public interface Connection {
	public long sendBlock(NumberedBlock block) throws BlockRefusedException, IOException, StreamClosedException;
	public void exchangePeer(PeerManager clientPeerHandler) throws IOException, StreamClosedException;
	public void challenge(Challenge challenge) throws IOException, StreamClosedException;
	public NumberedBlock requestBlock(long desiredBlockId) throws IOException, StreamClosedException;
	public void close() throws IOException;
    public Vector<HeaderBlock> requestAllHeaderBlock() throws IOException, StreamClosedException;
    public void invalidateBlockId(Long blockId) throws IOException, StreamClosedException;
	public void offerLocalFreeSpace(long localSpaceForPeer) throws IOException;
}

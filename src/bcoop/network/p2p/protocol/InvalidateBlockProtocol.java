/**
 * <p>Title: InvalidateBlockProtocol.java</p>
 * <p>Description: The procotol that allows the invalidation of block to remote peers.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bcoop.exception.StreamClosedException;
import bcoop.repos.BlockRepository;

/**
 * @author pmarches
 *
 */
public class InvalidateBlockProtocol extends Protocol {

    private BlockRepository blockRepository;
    private Long blockId;

    public InvalidateBlockProtocol(Long blockId, InputStream iStream, OutputStream oStream) throws IOException {
        super(Protocol.INVALIDATE_BLOCK, iStream, oStream);
        this.blockId = blockId;
    }

    public InvalidateBlockProtocol(BlockRepository blockRepository, InputStream iStream, OutputStream oStream) throws IOException {
        super(Protocol.INVALIDATE_BLOCK, iStream, oStream);
        this.blockRepository = blockRepository;
    }

    public CommandResult executeClient() throws IOException, StreamClosedException {
        initProtocol();
        
        ooStream.writeLong(this.blockId);
        ooStream.flush();
		return getResult();
    }

	public void executeServer() throws IOException, StreamClosedException {
        initProtocol();
        
        long blockId = oiStream.readLong();
        blockRepository.removeBlock(blockId);
        ooStream.writeObject(CommandResult.OK_RESULT);
    }

}

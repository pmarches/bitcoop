/**
 * <p>Title: BlockRequestProtocol.java</p>
 * <p>Description: Protocol to request blocks that havealready been sentto this host.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bcoop.block.NumberedBlock;
import bcoop.exception.StreamClosedException;
import bcoop.repos.BlockRepository;

/**
 * @author pmarches
 *
 */
public class BlockRequestProtocol extends Protocol {

    private long desiredBlockId;
    private NumberedBlock blockReceived;
    private BlockRepository blockRepository;

    /**
     * @param desiredBlockId 
     * @param iStream
     * @param oStream
     * @throws IOException
     */
    public BlockRequestProtocol(long desiredBlockId, InputStream iStream, OutputStream oStream) throws IOException {
        super(Protocol.REQUEST_BLOCK, iStream, oStream);
        this.desiredBlockId = desiredBlockId;
    }

    /**
     * @param mockBlockRepository
     * @param source
     * @param sink
     * @throws IOException 
     */
    public BlockRequestProtocol(BlockRepository blockRepository, InputStream source, OutputStream sink) throws IOException {
        super(Protocol.REQUEST_BLOCK, source, sink);
        this.blockRepository = blockRepository;
    }

    /* (non-Javadoc)
     * @see bcoop.network.p2p.protocol.Protocol#executeClient()
     */
    public CommandResult executeClient() throws IOException, StreamClosedException {
        try{
            initProtocol();
            
            ooStream.writeLong(this.desiredBlockId);
            ooStream.flush();
            
            this.blockReceived = (NumberedBlock) oiStream.readObject();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
		return CommandResult.OK_RESULT;
    }

    /* (non-Javadoc)
     * @see bcoop.network.p2p.protocol.Protocol#executeServer()
     */
    public void executeServer() throws IOException, StreamClosedException {
        initProtocol();

        long desiredBlockId = oiStream.readLong();
        NumberedBlock blockToSend = this.blockRepository.getBlock(desiredBlockId);
        ooStream.writeObject(blockToSend);
    }

    /**
     * @return
     */
    public NumberedBlock getReceivedBlock() {
        return this.blockReceived;
    }

}

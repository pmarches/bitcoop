/**
 * <p>Title: BlockRequestProtocol.java</p>
 * <p>Description: The protocol that allows the request of header blocks.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import bcoop.exception.StreamClosedException;
import bcoop.repos.BlockRepository;

/**
 * @author pmarches
 *
 */
public class HeaderBlockRequestProtocol extends Protocol {
    private Vector<Long> blocksReceived;
    private BlockRepository blockRepository;

    /**
     * @param iStream
     * @param oStream
     * @throws IOException
     */
    public HeaderBlockRequestProtocol(InputStream iStream, OutputStream oStream) throws IOException {
        super(Protocol.REQUEST_HEADER_BLOCK, iStream, oStream);
    }

    /**
     * @param mockBlockRepository
     * @param source
     * @param sink
     * @throws IOException 
     */
    public HeaderBlockRequestProtocol(BlockRepository blockRepository, InputStream source, OutputStream sink) throws IOException {
        super(Protocol.REQUEST_HEADER_BLOCK, source, sink);
        this.blockRepository = blockRepository;
    }

    /* (non-Javadoc)
     * @see bcoop.network.p2p.protocol.Protocol#executeClient()
     */
    public CommandResult executeClient() throws IOException, StreamClosedException {
        initProtocol();
        
        this.blocksReceived = new Vector<Long>(); 
        int nbHeader = oiStream.readInt();
        for(int i=0; i<nbHeader; i++){
            this.blocksReceived.add(oiStream.readLong());
        }
		return CommandResult.OK_RESULT;
    }

    /* (non-Javadoc)
     * @see bcoop.network.p2p.protocol.Protocol#executeServer()
     */
    public void executeServer() throws IOException, StreamClosedException {
        initProtocol();

        Vector<Long> blocksToSend = this.blockRepository.getAllHeaderBlockId();
        ooStream.writeInt(blocksToSend.size());
        for(int i=0; i<blocksToSend.size(); i++){
            ooStream.writeLong(blocksToSend.get(i));
        }
        ooStream.flush();
    }

    public Vector<Long> getReceivedHeaderBlock() {
        return blocksReceived;
    }
}

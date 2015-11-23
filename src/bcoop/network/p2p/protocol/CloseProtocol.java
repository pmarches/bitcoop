/**
 * <p>Title: CloseProtocol.java</p>
 * <p>Description: A protocol that allows a clean closing of a connection</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bcoop.exception.StreamClosedException;

/**
 * @author pmarches
 *
 */
public class CloseProtocol extends Protocol {

	/**
	 * @param type
	 * @param iStream
	 * @param oStream
	 * @throws IOException
	 */
	public CloseProtocol(InputStream iStream, OutputStream oStream) throws IOException {
		super(Protocol.CLOSE_CONNECTION, iStream, oStream);
	}

	/* (non-Javadoc)
	 * @see bcoop.network.p2p.protocol.Protocol#executeClient()
	 */
	public CommandResult executeClient() throws IOException, StreamClosedException {
		initProtocol();
		iStream.close();
		oStream.close();
		return CommandResult.OK_RESULT;
	}

	/* (non-Javadoc)
	 * @see bcoop.network.p2p.protocol.Protocol#executeServer()
	 */
	public void executeServer() throws IOException, StreamClosedException {
		initProtocol();
        iStream.close();
        oStream.close();
	}

}

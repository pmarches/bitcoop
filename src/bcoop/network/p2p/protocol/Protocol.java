/**
 * <p>Title: Protocol.java</p>
 * <p>Description: Common protocol information</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import bcoop.exception.StreamClosedException;

/**
 * @author pmarches
 *
 */
public abstract class Protocol{
	/*enum*/
	protected static final byte CLOSE_CONNECTION = 1;
	protected static final byte TRANSFERT_BLOCK = 2;
    protected static final byte REQUEST_BLOCK = 3;
	protected static final byte VERIFY_BLOCK = 4;
	protected static final byte PEER_EXCHANGE = 5;
	protected static final byte HANDSHAKE = 6;
    protected static final byte SEND_HEADER_BLOCK = 7;
    protected static final byte REQUEST_HEADER_BLOCK = 8;
    protected static final byte INVALIDATE_BLOCK = 9;
	protected static final byte ADJUST_SPACE = 10;

	protected ObjectOutputStream ooStream;
	protected OutputStream oStream;

	protected ObjectInputStream oiStream;
	protected InputStream iStream;

	private byte type;
	protected byte remoteType;
	private boolean readRemoteType = true;

	public Protocol(byte type, InputStream iStream, OutputStream oStream) throws IOException{
		this.type = type;
		this.iStream = iStream;
		this.oStream = oStream;
		
		//We cannot instanciate the ObjectIOStreams because they must be instanciated in the thread's execute context. 
	}
	
	public abstract CommandResult executeClient() throws IOException, StreamClosedException;
	public abstract void executeServer() throws IOException, StreamClosedException;
	
	protected void initProtocol()  throws IOException, StreamClosedException {
		oStream.write(type);
		oStream.flush();

		if(readRemoteType){
			readRemoteType();
		}

		ooStream = new ObjectOutputStream(oStream);
		oiStream = new ObjectInputStream(iStream);

	}

	private void readRemoteType() throws IOException, StreamClosedException {
		remoteType = (byte) iStream.read();
		if(remoteType == -1){
			throw new StreamClosedException();
		}
		if(remoteType != type){
			throw new IOException("Invalid remote end, we are "+ type + " the other is "+ remoteType);
		}
	}

	public boolean getReadRemoteType() {
		return readRemoteType;
	}
	public void setReadRemoteType(boolean readRemoteType) {
		this.readRemoteType = readRemoteType;
	}

	protected CommandResult getResult() throws IOException {
		try{
			CommandResult result = (CommandResult) this.oiStream.readObject();
			return result;
		} catch (ClassNotFoundException e) {
			throw new IOException("Did not find class:"+e.toString());
		}
	}
}

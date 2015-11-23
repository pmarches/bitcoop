package bcoop.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ByteArrayChannel implements  ReadableByteChannel{

	private byte[] srcData;
	int readIndex;
	private boolean isClosed;

	public ByteArrayChannel(byte[] srcData) {
		super();
		this.srcData = srcData;
		this.readIndex = 0;
		this.isClosed = false;
	}

	public int read(ByteBuffer destBuffer) throws IOException {
		if(this.readIndex>=this.srcData.length) return -1;
		
		int maxNbByteCanSend = Math.min(destBuffer.limit(), this.srcData.length-this.readIndex);
		destBuffer.clear();
		destBuffer.put(this.srcData, this.readIndex, maxNbByteCanSend);
		this.readIndex+=maxNbByteCanSend;
	
		return maxNbByteCanSend;
	}

	public boolean isOpen() {
		return isClosed;
	}

	public void close() throws IOException {
		this.isClosed = true;
	}

}

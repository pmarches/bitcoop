/**
 * <p>Title: PipeStream.java</p>
 * <p>Description: A FIFO pipe that you can read and write to. Gives you a pair of streams</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * @author pmarches
 *
 */
public class PipeStream {
	InputStream source = new Source();
	OutputStream sink = new Sink();
	LinkedList<Integer> data = new LinkedList<Integer>();
	private boolean endOfStream = false;
	
	class Source extends InputStream{
		int[] peekBuffer = null;
		boolean readFromPeekBuffer = false;
		private int currentPosition;
		
		public boolean markSupported(){
			return true;
		}
		
		public void mark(int readLimit){
			currentPosition = 0;
			peekBuffer = new int[readLimit];
			readFromPeekBuffer = false;
		}
		
		public void reset(){
			currentPosition = 0;
			readFromPeekBuffer = true;
		}
		
		public int available(){
			return data.size();
		}
		
		private int readFromPeekBuffer(){
			//Passed the read limit, so we cannot read from the buffer any more..
			if(peekBuffer != null && currentPosition == peekBuffer.length){
				readFromPeekBuffer = false;
				peekBuffer = null;
			}
			if(readFromPeekBuffer){
				int value = peekBuffer[currentPosition];
				currentPosition++;
				if(currentPosition == peekBuffer.length){
					readFromPeekBuffer = false;
				}
				return value;
			}
			return -1;
		}

		/* (non-Javadoc)
		 * @see java.io.InputStream#read()
		 */
		public int read() throws IOException {
			while(true){
				int peekedValue = readFromPeekBuffer();
				if(peekedValue != -1) return peekedValue;

				synchronized(data){
					if(data.isEmpty()){
						if(endOfStream){
							return -1;
						}
						try{
							data.wait(200);
						}
						catch(InterruptedException e){
							//Ok..
						}
					}
                    else{
                        int dataToReturn = data.removeFirst();
                        if(peekBuffer != null){
                        	peekBuffer[currentPosition]=dataToReturn;
                        	currentPosition++;
                        }
                        return dataToReturn;
                    }
				}
			}
		}
	}
	
	class Sink extends OutputStream{
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			if(endOfStream){
				throw new IOException("Stream is closed");
			}
			synchronized(data){
				data.addLast(b);
				data.notifyAll();
			}
		}
		
		public void close() throws IOException{
			endOfStream = true;
			super.close();
		}
	}
	
	public InputStream getSource(){
		return source;
	}
	
	public OutputStream getSink(){
		return sink;
	}
}

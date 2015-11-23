/**
 * <p>Title: PipeStreamTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class PipeStreamTest extends TestCase {
    String testData = "Allo mon grow";
    PipeStream pipe;
    
    public void setUp(){
        pipe = new PipeStream();
    }

    class ThreadReader extends Thread{
        private InputStream source;

        public ThreadReader(InputStream source){
            super("ThreadReader");
            this.source  = source;
        }
        
        public void run(){
            try {
                while(source.read() != -1);
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }
        }
    }

    public void testThreadSafeRead(){
        ThreadReader reader = new ThreadReader(pipe.getSource());
        reader.start();
        
        try{
            for(int i=0 ; i< 10; i++){
                pipe.getSink().write(testData.getBytes());
                if(i%2 == 1) Thread.yield();
            }
            pipe.getSink().close();
        }
        catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        try {
            reader.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            fail();
        }
        
    }

	public void testPipeStream(){
		try{
			pipe.getSink().write(testData.getBytes());
			
			byte[] readData = new byte[testData.length()]; 
			pipe.getSource().read(readData);
			
			assertEquals(testData, new String(readData));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public void testPipeStreamObjects(){
		try{
			ObjectOutputStream oos = new ObjectOutputStream(pipe.getSink());
			oos.writeObject(testData);
			oos.writeByte(34);
			oos.flush();
			oos.close();
			
			ObjectInputStream ois = new ObjectInputStream(pipe.getSource());
			String readData = (String) ois.readObject();
			assertEquals(34, ois.readByte());
			
			assertEquals(testData, readData);
			assertEquals(-1, ois.read());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void testMarkReset(){
		try{
			int[] data = new int[]{1,4,67,22,167};
			InputStream iStream = pipe.getSource();
			OutputStream oStream = pipe.getSink();
			for(int i=0; i<data.length; i++){
				oStream.write(data[i]);
			}
			for(int i=0; i<data.length; i++){
				oStream.write(data[i]);
			}
			oStream.close();
			
			assertTrue(iStream.markSupported());

			iStream.mark(data.length);
			for(int i=0; i<data.length; i++){
				assertEquals(data[i], iStream.read());
			}
			iStream.reset();
			for(int i=0; i<data.length; i++){
				assertEquals(data[i], iStream.read());
			}
			for(int i=0; i<data.length; i++){
				assertEquals(data[i], iStream.read());
			}
			
			assertEquals(-1, iStream.read());
			
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
}

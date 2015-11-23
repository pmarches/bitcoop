/**
 * <p>Title: BlockIdFactory.java</p>
 * <p>Description: Generates unique blockIds for this host.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.blockfactory;

import java.io.RandomAccessFile;

/**
 * @author pmarches
 *
 */
public class BlockIdFactory {
	private long nextBlockId; 
	private static BlockIdFactory instance;
	private static final String BLOCK_ID_FACTORY_FILENAME = "blockIdFactory.dat";
	RandomAccessFile raf;
	
	private BlockIdFactory(String filename){
		try{
			raf = new RandomAccessFile(BLOCK_ID_FACTORY_FILENAME, "rw");
		}
		catch(Exception e){
			e.printStackTrace();
		}

		try{
			nextBlockId = raf.readLong();
		}
		catch(Exception e){
			nextBlockId = 1;
		}
	}
	
	public static BlockIdFactory getinstance(){
		if(instance == null){
			instance = new BlockIdFactory(BLOCK_ID_FACTORY_FILENAME);
		}
		return instance;
	}
	
	public synchronized long getNewBlockId(){
		nextBlockId++;
		try{
			raf.seek(0);
			raf.writeLong(nextBlockId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return nextBlockId;
	}
}

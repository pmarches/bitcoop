/**
 * <p>Title: BlockFactoryContext.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.blockfactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.crypto.Encryptor;
import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class BlockFactory {
	long currentPos;
	static final int DEFAULT_MAX_BLOCK_SIZE = 1000000;
    int maxBlockSize=DEFAULT_MAX_BLOCK_SIZE;
    BitCoopFile theFile;
	FileInputStream fis;
    HeaderBlock hBlock;
	private FileChannel fileChannel;
    ZipBlockProcessor zProc = new ZipBlockProcessor();
	private Encryptor blockEncryptor;
    
	/**
	 * @param fileContext
	 * @param block_size
	 * @throws FileNotFoundException 
	 * @throws FileNotFoundException 
	 */
    public BlockFactory(SecretKey encrpytionKey, String filename) throws FileNotFoundException{
        	this.blockEncryptor = new Encryptor(encrpytionKey);
	    	this.currentPos = 0;
	    	
	    	this.theFile = new BitCoopFile(filename);
	    	this.hBlock = new HeaderBlock(BlockIdFactory.getinstance().getNewBlockId(), filename, theFile.lastModified());
	    	if(theFile.isFile()){
	    		this.fis = new FileInputStream(theFile);
	    		this.fileChannel = this.fis.getChannel();
	    		this.hBlock.setFSObjectType(HeaderBlock.FS_FILE_OBJECT);
	    	}
	    	else if(theFile.isDirectory()){
	    		this.hBlock.setFSObjectType(HeaderBlock.FS_DIRECTORY_OBJECT);
	    	}
	    	else if(theFile.isLink()){
	    		this.hBlock.setFSObjectType(HeaderBlock.FS_FILE_OBJECT);
	    		this.hBlock.setProperty(HeaderBlock.PROPNAME_LINK_DEST, this.theFile.getLinkDestination());
	    	}
	    	else{
	    		throw new RuntimeException("File type not supported :"+filename);
	    	}
	    	//TODO add mode, permissions, etc...
    }
    
    public HeaderBlock getHeaderBlock() throws IOException{
        if(hasNextBlock()){
            throw new RuntimeException("You should get the headerblock only after getting all the DataBlocks.");
        }
        this.hBlock.setEncryptionIV(blockEncryptor.getIV());
        hBlock.setBackupTime(System.currentTimeMillis());
        return hBlock;
    }

	/**
	 * @return
	 * @throws IOException 
	 * @throws IOException 
	 */
	public boolean hasNextBlock() throws IOException{
		if(theFile.isFile() == false){
			return false;
		}
		if(currentPos >= theFile.length()){
			return false;
		}

		return true;
	}

	/**
	 * @return
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws ShortBufferException 
	 */
	public DataBlock nextBlock() throws IOException, IllegalBlockSizeException, BadPaddingException, ShortBufferException {
		long blockId = BlockIdFactory.getinstance().getNewBlockId();

        int zippedBlockSize = Math.min((int)(theFile.length()-currentPos)+ZipBlockProcessor.SIZE_OF_ZIP_HEADER, maxBlockSize);
        byte[] zippedData = zProc.compressBlock(this.fileChannel, zippedBlockSize);
        long newSourcePosition = zProc.getSourceChannelOffset();

        byte[] cipheredData = this.blockEncryptor.encrypt(zippedData);
        DataBlock block = new DataBlock(blockId, cipheredData); //FIXME Allocate a new array for each block?
        
        hBlock.registerDataBlock(block, currentPos, (int) (newSourcePosition-currentPos));

        currentPos=newSourcePosition;
		return block;
	}

    public int getMaxBlockSize() {
        return maxBlockSize;
    }
    public void setMaxBlockSize(int maxBlockSize) {
        this.maxBlockSize = maxBlockSize;
    }
}

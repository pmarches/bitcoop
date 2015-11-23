/**
 * <p>Title: BlockRepository.java</p>
 * <p>Description: Base class that allows torage of blocks.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.repos;

import java.util.Vector;

import org.apache.log4j.Logger;

import bcoop.block.NumberedBlock;
import bcoop.exception.StorageLimitException;

/**
 * @author pmarches
 *
 */
public abstract class BlockRepository {
    public final static long UNLIMITED_SPACE_ALLOWED=-1;
    public final static long NO_SPACE_ALLOWED=0;
    public static final long ALLOWED_SPACE_DELTA = 10000000; //This is the maximum allowed difference of space (in bytes) between shared space and used space 
    protected long allowedSpace=NO_SPACE_ALLOWED;
    RepositoryManager reposManager;

    public BlockRepository(RepositoryManager reposManager){
    	this.reposManager=reposManager;
    }

    /**
     * @param newBlock
     */
    abstract public void storeBlock(NumberedBlock newBlock) throws StorageLimitException;

    /**
     * @param blockId
     * @return
     * @throws IOException 
     */
    abstract public NumberedBlock getBlock(Long blockId);
    abstract public Vector<Long> getAllHeaderBlockId();
    abstract public Vector<Long> getAllTransactionBlockId();
    abstract public void destroyRepository();
    abstract public long getUsedSpace();
    abstract public void removeBlock(long blockId);

    public boolean canStoreBlockSize(long blockSize) {
    		if(getAllowedSpace()==UNLIMITED_SPACE_ALLOWED){
    			return true;
    		}
        return getUsedSpace()+blockSize <= getAllowedSpace()+ALLOWED_SPACE_DELTA;
    }

    public void increaseAllowedSpace(long size) {
        if(size < 0){
            Logger.getLogger(this.getClass()).error("Invalid size to increaseAllowedSpace "+size);
            return;
        }
        allowedSpace+=size;
    }

    public void decreaseAllowedSpace(long size) {
        if(size < 0){
            Logger.getLogger(this.getClass()).error("Invalid size to decreaseAllowedSpace "+size);
            return;
        }
        allowedSpace-=size;
    }

    public long getAllowedSpace() {
        return allowedSpace;
    }
    
    public void setAllowedSpace(long newAllowedSpace) throws StorageLimitException {
    	if(this.reposManager != null && this.reposManager.hasGlobalMaximumAllowedSpace()){
    		long futureGlobalAllowedSpace = this.reposManager.getLocalSpaceUsedByAllPeers() - this.getAllowedSpace() + newAllowedSpace;
    		if(futureGlobalAllowedSpace > this.reposManager.getGlobalMaximumAllowedSpace()){
    			throw new StorageLimitException("The global limit has been reached!");
    		}
    	}

    	allowedSpace = newAllowedSpace;
    }
}
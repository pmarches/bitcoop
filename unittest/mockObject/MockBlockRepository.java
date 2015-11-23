/**
 * <p>Title: MemoryBlockRepository.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package mockObject;

import java.util.Vector;
import java.util.Hashtable;

import bcoop.block.HeaderBlock;
import bcoop.block.NumberedBlock;
import bcoop.block.TransactionBlock;
import bcoop.repos.BlockRepository;
import bcoop.repos.RepositoryManager;

/**
 * @author pmarches
 *
 */
public class MockBlockRepository extends BlockRepository {

	public Hashtable<Long, NumberedBlock> blocks = new Hashtable<Long, NumberedBlock>();
    public Vector<Long> hBlocks = new Vector<Long>();
    public Vector<Long> tBlocks = new Vector<Long>();
    public Hashtable<Long, NumberedBlock> removedBlocks = new Hashtable<Long, NumberedBlock>();
    public long usedSpace=0;
    
	public MockBlockRepository() {
		super(null);
	}

    synchronized public void storeBlock(NumberedBlock newBlock) {
        blocks.put(newBlock.getBlockId(), newBlock);
        if(newBlock instanceof HeaderBlock){
            hBlocks.add(newBlock.getBlockId());
        }
        else if(newBlock instanceof TransactionBlock){
            tBlocks.add(newBlock.getBlockId());
        }
        usedSpace+=newBlock.getStorageSizeOfBlock();
        notifyAll();
    }

    synchronized public NumberedBlock getBlock(Long blockId) {
        if(!blocks.containsKey(blockId)){
            throw new RuntimeException("No such block id "+blockId);
        }
        return blocks.get(blockId);
    }

    synchronized public Vector<Long> getAllHeaderBlockId() {
        return hBlocks;
    }

    public long getUsedSpace() {
        return usedSpace;
    }

    public boolean canStoreBlockSize(long blockSize) {
        return true;
    }

    synchronized public void destroyRepository() {
        blocks.clear();
        hBlocks.clear();
    }

    synchronized public void removeBlock(long blockId) {
        removedBlocks.put(blockId, blocks.get(blockId)); 

        blocks.remove(blockId);
        hBlocks.remove(blockId);
        tBlocks.remove(blockId);
        notifyAll();
    }
    
    synchronized public void waitForBlock(int nbBlocks) {
        while(blocks.size() < nbBlocks){
            try {
                wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized public void waitForRemovedBlock(int nbBlocks) {
        while(removedBlocks.size() < nbBlocks){
            try {
                wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public Vector<Long> getAllTransactionBlockId() {
		return tBlocks;
	}

}

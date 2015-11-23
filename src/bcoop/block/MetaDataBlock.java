/**
 * <p>Title: MetaDataBlock.java</p>
 * <p>Description: This block contains information about other blocks of data.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.block;

import java.io.Serializable;
import java.util.Vector;

@javax.persistence.Entity
public class MetaDataBlock extends NumberedBlock implements Serializable{
    private static final long serialVersionUID = 2705851265247113563L;

    byte[] hash;
    long offset;
    int size;

	public MetaDataBlock(long blockId, long offset, int size) {
		super(blockId);
        this.offset = offset;
        this.size = size;
    }
	
    public long getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }
    public long getBlockId() {
        return blockId;
    }
    public byte[] getHash() {
        return hash;
    }

	@Override
	public void getReferencedBlock(Vector<Long> referencedBlocks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getStorageSizeOfBlock() {
		// TODO Auto-generated method stub
		return 0;
	}
}
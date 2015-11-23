/**
 * <p>Title: DataBlock.java</p>
 * <p>Description: A block of data that can be sent over the network.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.block;

import java.util.Arrays;
import java.util.Vector;

import javax.persistence.Transient;

/**
 * @author pmarches
 *
 */
@javax.persistence.Entity
public class DataBlock extends NumberedBlock {
	private static final long serialVersionUID = 1375484761187994109L;

	@Transient
	byte[] dataContent;
	
    public DataBlock(){
        super(-1);
    }
    
	public DataBlock(long blockId, byte[] blockData){
		super(blockId);
		this.dataContent = blockData;
	}
	
	public DataBlock(long blockId, int allocatedSize) {
		super(blockId);
		this.dataContent = new byte[allocatedSize];
	}

	public byte[] getBlockData(){
		return dataContent;
	}

	public void setBlockData(byte[] data) {
		this.dataContent = data;
	}

    /**
     * @return
     */
    public int getStorageSizeOfBlock() {
        if(dataContent == null) return 0;
        return dataContent.length;
    }

	@Override
	public void getReferencedBlock(Vector<Long> referencedBlocks) {
		return;
	}
	
	public boolean equals(Object obj2){
		if(super.equals(obj2)==false) return false;
		DataBlock dBlock2 = (DataBlock) obj2;
		return Arrays.equals(this.dataContent, dBlock2.dataContent);
	}
}

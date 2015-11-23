/**
 * <p>Title: NumberedBlock.java</p>
 * <p>Description: Basic block that has a number as identifier</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.block;

import java.io.Serializable;
import java.util.Vector;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.InheritanceType;

/**
 * @author pmarches
 */
@javax.persistence.Entity
@javax.persistence.Inheritance(strategy=InheritanceType.JOINED)
public abstract class NumberedBlock implements Serializable{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    protected long blockId;

    public NumberedBlock(long blockId){
        this.blockId = blockId;
    }
    
    public long getBlockId(){
        return blockId;
    }

    public boolean equals(Object obj2){
		NumberedBlock dBlock2 = (NumberedBlock) obj2;
		return (this.blockId == dBlock2.blockId);
	}

    abstract public int getStorageSizeOfBlock();
    abstract public void getReferencedBlock(Vector<Long> referencedBlocks);
}

/**
 * <p>Title: HeaderBlock.java</p>
 * <p>Description: A header block contains information about others blocks for a single file</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.block;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import org.hibernate.annotations.CollectionOfElements;

/**
 * @author pmarches
 *
 */
@javax.persistence.Entity
public class HeaderBlock extends NumberedBlock {
    private static final long serialVersionUID = -3225711231166885232L;
	public static final byte FS_FILE_OBJECT = 1;
	public static final byte FS_DIRECTORY_OBJECT = 2;
	public static final byte FS_LINK_OBJECT = 3;
	public static final byte FS_BLOCKDEV_OBJECT = 4;
	
	public static final String PROPNAME_FS_TYPE="fsType";
	public static final String PROPNAME_LINK_DEST = "linkDest";
	public static final String PROPNAME_MODE = "mode";
	
	String filename;
	@CollectionOfElements
    Map<Long, MetaDataBlock> metaDataBlocks = new Hashtable<Long, MetaDataBlock>();
	@CollectionOfElements
    Map<String, String> fileProperties = new Hashtable<String, String>();

    private long backupTime;
    private byte[] encryptionIV;
//    TODO Hash for the whole file.
    
    /**
     * @param filename
     */
    public HeaderBlock(long blockId, String filename, long backupTime) {
        super(blockId);
        this.filename = filename;
        this.backupTime = backupTime;
    }

    /**
     * @param blockId
     */
    public void registerDataBlock(DataBlock block, long offset, int uncompresssedSize) {
        MetaDataBlock metaDataBlock = new MetaDataBlock(block.getBlockId(), offset, uncompresssedSize);
        metaDataBlocks.put(block.getBlockId(), metaDataBlock);
    }

    /**
     * @param blockId
     * @return
     */
    public void checkValidBlock(DataBlock block) {
        MetaDataBlock dBlockInfo = getMetaDataForDataBlock(block.getBlockId());
        if(dBlockInfo == null){
        	throw new RuntimeException("This block does not have associated meat info");
        }
    }
    
    public MetaDataBlock getMetaDataForDataBlock(long blockId) {
        MetaDataBlock dBlockInfo = metaDataBlocks.get(blockId);
        if(dBlockInfo == null){
            throw new RuntimeException("That block does not belong in this HeaderBlock..");
        }
        return dBlockInfo;
    }

    /**
     * @return
     */
    public String getFilename() {
        return filename;
    }

    public LinkedList<Long> getAssociatedDataBlockId() {
	    	LinkedList<Long> blockIds = new LinkedList<Long>();
	    	Set<Long> keys = metaDataBlocks.keySet();
	    	blockIds.addAll(keys);
	    	return blockIds;
    }

    public long getBackupTime() {
        return backupTime;
    }

    //FIXME This is confusing with the getFilelenght method! Thisshould return the size of this header bvlock on the repository
    public int getStorageSizeOfBlock() {
        return 0;
    }

    public long getFileLength() {
        long length = 0;
        Iterator<MetaDataBlock> e = metaDataBlocks.values().iterator();
        while(e.hasNext()){
            MetaDataBlock mBlock = e.next();
            length+=mBlock.getSize();
        }
        return length;
    }

	@Override
	public void getReferencedBlock(Vector<Long> referencedBlocks) {
		referencedBlocks.addAll(metaDataBlocks.keySet());
	}

	public void setBackupTime(long time) {
		this.backupTime = time;
	}

	public byte getFSObjectType() {
		return Byte.parseByte(getProperty(PROPNAME_FS_TYPE));
	}
	
	public void setFSObjectType(byte fs_file_object) {
		fileProperties.put(PROPNAME_FS_TYPE, Byte.toString(fs_file_object));
	}
	
	public String getProperty(String propName){
		return fileProperties.get(propName);
	}
	public void setProperty(String propName, String propValue){
		fileProperties.put(propName, propValue);
	}

	public byte[] getEncryptionIV() {
		return encryptionIV;
	}

	public void setEncryptionIV(byte[] encryptionIV) {
		if(encryptionIV == null){
			throw new RuntimeException("Invalid encryption IV");
		}
		this.encryptionIV = encryptionIV;
	}
}

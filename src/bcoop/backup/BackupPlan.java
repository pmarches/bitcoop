/**
 * <p>Title: BackupPlan.java</p>
 * <p>Description: A plan indicating where each block of each file will be sent</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup;

import java.util.Vector;

import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class BackupPlan {
	
	private BitCoopFile fileContext;
    private boolean requiresBackup = true;
    Vector<Long> blockToInvalidate = new Vector<Long>(); 

	public BackupPlan(BitCoopFile fileContext){
		this.fileContext = fileContext;
	}

	/**
	 * @return
	 */
	public BitCoopFile getFileContext() {
		return fileContext;
	}

	/**
	 * @return
	 */
	public String getFilename() {
		return fileContext.getAbsolutePath();
	}

    public void setRequiresBackup(boolean requiresBackup) {
        this.requiresBackup = requiresBackup;        
    }

    public boolean requiresBackup() {
        return this.requiresBackup;
    }

    public void addInvalidatedBlock(long oldBlockId) {
        this.blockToInvalidate.add(oldBlockId);        
    }

    public Vector<Long> getInvalidatedBlock() {
        return this.blockToInvalidate;
    }
}

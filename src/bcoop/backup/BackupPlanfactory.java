/**
 * <p>Title: BackupPlanfactory.java</p>
 * <p>Description: BackuPlan factory pattern.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup;

import bcoop.block.HeaderBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.exception.NoPeerAvailableException;
import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class BackupPlanfactory {
    private BlockTracker bTracker;

    public BackupPlanfactory(BlockTracker bTracker){
    		this.bTracker = bTracker;
    }
	
	/**
	 * @param context
	 * @return
	 * @throws NoPeerAvailableException 
	 */
	public BackupPlan createBackupPlan(BitCoopFile file) throws NoPeerAvailableException {
        BackupPlan plan = new BackupPlan(file);
        plan.setRequiresBackup(requiresBackup(file));
		return plan;
	}
    
    /*junit*/ boolean requiresBackup(BitCoopFile fileToCheck) {
        //Check with the tracker to get the last HeaderBlock associated with the file
        HeaderBlock hBlock = this.bTracker.getLatestHeaderBlockForFile(fileToCheck.getAbsolutePath());
        if(hBlock == null) return true; //File has never been backedup..
        
        //TODO Check with the hash??
        if(fileToCheck.lastModified() >= hBlock.getBackupTime() || fileToCheck.length() != hBlock.getFileLength()){
            return true; //Backup is out of date..
        }
        return false;
    }

}

/**
 * <p>Title: RestoreJob.java</p>
 * <p>Description: A class that stores information about a restoration job</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.restoration;

import java.util.LinkedList;

import bcoop.block.HeaderBlock;

/**
 * @author pmarches
 *
 */
public class RestoreJob {
    private String restoreDir;
    private LinkedList<HeaderBlock> hblocksToRestore = new LinkedList<HeaderBlock>();
    
    public RestoreJob(String restoreDir){
        this.restoreDir = restoreDir;
    }
    
    public String getRestoreDir() {
        return restoreDir;
    }
    public void setRestoreDir(String restoreDir) {
        this.restoreDir = restoreDir;
    }

    public void addHeaderBlockToRestore(HeaderBlock newBlock){
    		hblocksToRestore.add(newBlock);
    }
    
	public LinkedList<HeaderBlock> getHeaderBlockList() {
		return hblocksToRestore;
	}

}

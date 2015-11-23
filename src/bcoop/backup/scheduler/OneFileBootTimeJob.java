/**
 * <p>Title: OneTimeSchedule.java</p>
 * <p>Description: A special schedule that runs only once, at boottime and backsup a single file</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import bcoop.backup.fileselection.FileSelection;
import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class OneFileBootTimeJob extends ScheduledJob {

	private static final long serialVersionUID = 7445012958452621184L;
	private String filename;

	public OneFileBootTimeJob(String filename){
		this.filename = filename;
        addSchedule(new NowSchedule());
	}
    
    public FileSelection getFileSelection(){
        FileSelection oneFile = new FileSelection();
        oneFile.add(new BitCoopFile(filename));
        
        return oneFile;
    }

	/* (non-Javadoc)
	 * @see bcoop.scheduler.Schedule#willRunAgain()
	 */
	public boolean willRunAgain() {
		return false;
	}


    /* (non-Javadoc)
     * @see bcoop.backup.scheduler.Job#getName()
     */
    public String toString() {
        return "Bootime "+filename;
    }
	
}

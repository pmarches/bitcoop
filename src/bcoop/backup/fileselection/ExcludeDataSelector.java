/**
 * <p>Title: ExcludeDataSelector.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.fileselection;

import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class ExcludeDataSelector extends DataSelector {
	private static final long serialVersionUID = -7297475800832545862L;
	
	/**
	 * @param pattern
	 * @param size
	 */
	public ExcludeDataSelector(String pattern, String size) {
		super(pattern, size);
	}
	
	/* (non-Javadoc)
	 * @see bcoop.backup.fileselection.DataSelector#expandToFiles(java.util.Vector)
	 */
	public void expandToFiles(FileSelection filesToBackup) {
		for(int i=0; i< filesToBackup.size(); i++){
			BitCoopFile aFile = filesToBackup.get(i);
			if(matches(aFile)){
				filesToBackup.remove(aFile);
				i--;
			}
		}
	}
}

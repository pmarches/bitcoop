/**
 * <p>Title: FileSelection.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.fileselection;

import java.util.Vector;
import java.util.Iterator;

import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class FileSelection extends Vector<BitCoopFile> {
	private static final long serialVersionUID = -1106790563035864218L;
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		Iterator<BitCoopFile> it = iterator();
		while(it.hasNext()){
			BitCoopFile f = it.next();
			buffer.append(f.getAbsolutePath());
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public long getTotalSize() {
		long totalSize=0;
		Iterator<BitCoopFile> it = iterator();
		while(it.hasNext()){
			BitCoopFile f = it.next();
			totalSize+=f.length();
		}
		return totalSize;
	}
}

/**
 * <p>Title: IncludeDataSelector.java</p>
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
public class IncludeDataSelector extends DataSelector {
	private static final long serialVersionUID = 7074125776215786895L;
	
	/**
	 * @param pattern
	 * @param size
	 */
	public IncludeDataSelector(String pattern, String size) {
		super(pattern, size);
	}
	
	public void expandToFiles(FileSelection fileList) {
		BitCoopFile[] roots;
		
		if(pattern.startsWith(".")){ //Dot as a regex, not a ./ or ..
			roots = BitCoopFile.listRoots();
		}
		else{
			int wildCardStart = pattern.indexOf("*");
			int pathEnd = pattern.lastIndexOf("/", wildCardStart);
			roots = new BitCoopFile[]{new BitCoopFile(pattern.substring(0, pathEnd))};
		}
		
		expand(roots, fileList);
	}
	
	private void expand(BitCoopFile[] roots, FileSelection backupList) {
		for(BitCoopFile root : roots){
			if(matches(root) && (root.isFile() || root.isLink())){
				backupList.add(root);
			}
			else if(root.isDirectory()){
				BitCoopFile[] childs = root.listFiles();
				if(childs == null){ //Empty directory
					backupList.add(root);
				}
				else{
					expand(childs, backupList);
				}
			}
		}
	}
	
}

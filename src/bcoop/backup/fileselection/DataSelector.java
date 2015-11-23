/**
 * <p>Title: DataSelector.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.fileselection;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public abstract class DataSelector implements Serializable{
    Pattern sizePatt = Pattern.compile("([+-])(\\d+)([KMG]?)");

    String pattern;
    String size;
    
    /**
     * @param pattern
     * @param size
     */
    public DataSelector(String pattern, String size) {
		if(pattern != null && pattern.length() != 0){
			boolean patternIsRelativePath = true;
			for(BitCoopFile root : BitCoopFile.listRoots()){
				if(pattern.startsWith(root.getAbsolutePath())){
					patternIsRelativePath = false;
					break;
				}
			}
			if(pattern.startsWith(".")){
				patternIsRelativePath = false;
			}
			if(patternIsRelativePath){
				//Standardize all path to use a forward slash.
				pattern = new BitCoopFile(System.getProperty("user.dir")).getAbsolutePath()+'/'+pattern;
			}
	        this.pattern = pattern;
		}

		this.size = size;
    }

    public boolean matches(BitCoopFile file){
        boolean patternEval = false;
        if(pattern == null || pattern.length() == 0){
            patternEval = true; //Default ignore file pattern
        }
        else{
            patternEval = file.getAbsolutePath().matches(pattern);
        }
        
        boolean sizeEval =  evalSize(file);
        
        return sizeEval && patternEval;
    }

    private boolean evalSize(BitCoopFile file) {
        if(size == null || size.length() == 0){
            return true; //Default any size
        }
        
        boolean sizeEval = false;
        Matcher matches = sizePatt.matcher(size.toUpperCase());
        if(matches.matches() == false){
            throw new RuntimeException("'"+size+"'Invalid size syntax.. must match the regex: "+sizePatt.pattern()); 
        }
        long size = Integer.parseInt(matches.group(2));
        if(matches.groupCount() == 3){
            if("K".equals(matches.group(3))){
                size = size * 1024;
            }
            else if("M".equals(matches.group(3))){
                size = size * 1024 * 1024;
            }
            else if("G".equals(matches.group(3))){
                size = size * 1024 * 1024 * 1024;
            }
        }

        if("+".equals(matches.group(1))){
            sizeEval = (file.length() >= size);
        }
        else if("-".equals(matches.group(1))){
            sizeEval = (file.length() <= size);
        }

        return sizeEval;
    }
    
    abstract public void expandToFiles(FileSelection filesToBackup);

    public String getPattern() {
        return pattern;
    }
}

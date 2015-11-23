/**
 * <p>Title: MockFile.java</p>
 * <p>Description: A bogus file that can be used to test backups.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package mockObject;

import bcoop.util.BitCoopFile;

/**
 * @author pmarches
 *
 */
public class MockFile extends BitCoopFile {
    private static final long serialVersionUID = 2405355915711188192L;
	public long size;
    public long modificationTime;

    public MockFile(String pathname, long size, long modificationTime) {
        super(pathname);
        this.size = size;
        this.modificationTime = modificationTime;
    }
    
    public long length(){
        return this.size;
    }
    
    public long lastModified(){
        return modificationTime;
    }
}

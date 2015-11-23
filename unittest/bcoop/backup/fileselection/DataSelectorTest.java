/**
 * <p>Title: DataSelectorTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.fileselection;

import mockObject.MockFile;

import bcoop.backup.fileselection.DataSelector;
import bcoop.util.BitCoopFile;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class DataSelectorTest extends TestCase {
    private static final String LINK_TO_DIR = "testData/filesToBackup/linkToDir";
    private static final String LINK_TO_FILE = "testData/filesToBackup/linkToFile";

	public final void testMatches() {
		String root = BitCoopFile.listRoots()[0].getAbsolutePath();
        DataSelector isoPlus100M = new IncludeDataSelector(".*\\.iso", "+100M");
        assertTrue(isoPlus100M.matches(new MockFile(root+"/tmp/toto.iso", 650L*1024*1024, 0)));
        assertFalse(isoPlus100M.matches(new MockFile(root+"/tmp/toto.coco", 650L*1024*1024, 0)));
        assertFalse(isoPlus100M.matches(new MockFile(root+"/tmp/toto.iso", 650L*1024, 0)));

        DataSelector excludeBigIso = new ExcludeDataSelector(".*\\.iso", "+100G");
        assertTrue(excludeBigIso.matches(new MockFile(root+"/tmp/toto.iso", 650L*1024*1024*1024, 0)));
        assertFalse(excludeBigIso.matches(new MockFile(root+"/tmp/iso.coco", 650L*1024*1024*1024, 0)));
        assertFalse(excludeBigIso.matches(new MockFile(root+"/tmp/toto.iso", 650L*1024*1024, 0)));

        DataSelector excludeTmp = new ExcludeDataSelector(root+"tmp/.*", null);
        assertFalse(excludeTmp.matches(new MockFile(root+"toto/tmp.txt", 100, 0)));
        assertTrue(excludeTmp.matches(new MockFile(root+"tmp/toto/tmp.txt", 100, 0)));
    }
    
    public final void testExpandToFile(){
		int nbLinks = 0;
    	if(new BitCoopFile(LINK_TO_DIR).exists()){
    		nbLinks+=1;
    	}
    	if(new BitCoopFile(LINK_TO_FILE).exists()){
    		nbLinks+=1;
    	}
    	
        DataSelector allFiles = new IncludeDataSelector("testData/filesToBackup/.*", null);
        FileSelection filesToBackup = new FileSelection(); 
        allFiles.expandToFiles(filesToBackup);
        assertEquals("NbLinks="+nbLinks+" size="+filesToBackup.size(), 54+nbLinks, filesToBackup.size());

        DataSelector removeCVS = new ExcludeDataSelector(".*/CVS/.*", null);
        removeCVS.expandToFiles(filesToBackup);
        DataSelector removeSVN = new ExcludeDataSelector(".*/.svn/.*", null);
        removeSVN.expandToFiles(filesToBackup);
        assertEquals("NbLinks="+nbLinks+" size="+filesToBackup.size(), 8+nbLinks, filesToBackup.size());
        
        DataSelector removeCrap = new ExcludeDataSelector(".*crap.*", null);
        removeCrap.expandToFiles(filesToBackup);
        assertEquals("NbLinks="+nbLinks+" size="+filesToBackup.size(), 6+nbLinks, filesToBackup.size());
        
        DataSelector removePunch = new ExcludeDataSelector(".*\\.punch", null);
        removePunch.expandToFiles(filesToBackup);
        assertEquals(filesToBackup.toString(), 5+nbLinks, filesToBackup.size());
        
        DataSelector removeBig = new ExcludeDataSelector(null, "+1000");
        removeBig.expandToFiles(filesToBackup);
        assertEquals("NbLinks="+nbLinks+" size="+filesToBackup.size(), 3+nbLinks, filesToBackup.size());
        
    }

}

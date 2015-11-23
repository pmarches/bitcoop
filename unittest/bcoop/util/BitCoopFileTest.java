package bcoop.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import junit.framework.TestCase;

public class BitCoopFileTest extends TestCase {

	public void testIsThisOrThat() {
		BitCoopFile bFile = new BitCoopFile("/usr/lib/libcom_err.dylib");
		if(bFile.exists()){ //FIXME Add test on windows/unix
			assertTrue(bFile.isLink());
			assertEquals("/System/Library/Frameworks/Kerberos.framework/Kerberos", bFile.getLinkDestination());
			assertFalse(bFile.isDirectory());
			
			bFile = new BitCoopFile("/usr/bin");
			assertFalse(bFile.isLink());
			assertTrue(bFile.isDirectory());
			assertNull(bFile.getLinkDestination());
			
			bFile = new BitCoopFile("/etc/passwd");
			assertFalse(bFile.isLink());
			assertFalse(bFile.isDirectory());
			assertTrue(bFile.isFile());
			assertNull(bFile.getLinkDestination());
		}
	}
	
	public void testListFileLink() throws IOException{
		BitCoopFile tFile = BitCoopFile.createTempFile("www","");
		tFile.deleteOnExit();
		String str = tFile.getAbsolutePath();
		BitCoopFile bFile = new BitCoopFile(str);
		assertTrue(new File(str).isFile());
		assertTrue(bFile.isFile());
	
		if(OSCapabilities.isLinkSupported()){
			tFile = BitCoopFile.createTempFile("Ž??","ˆˆ");
			tFile.deleteOnExit();
			str = tFile.getAbsolutePath();
			bFile = new BitCoopFile(str);
			assertTrue(bFile.isFile());
		}
	}
	
	public void testCreateLink() throws IOException{
		if(OSCapabilities.isLinkSupported()){
			BitCoopFile tFile = BitCoopFile.createTempFile("www","");
			tFile.deleteOnExit();
			tFile.delete();
			String str = tFile.getAbsolutePath();
			BitCoopFile bFile = new BitCoopFile(str);
			bFile.createSymbolicLink("/tmp");
	
			bFile = new BitCoopFile(str);
			assertTrue(bFile.isLink());
			assertEquals("/tmp", bFile.getLinkDestination());
		}
	}

	public void testSetMode(){
		//TODO
	}
}

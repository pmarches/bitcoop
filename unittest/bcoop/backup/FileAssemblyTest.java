/**
 * <p>Title: FileAssemblyTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import mockObject.MockIdentityManager;

import bcoop.AllTests;
import bcoop.assembler.BlockAssembler;
import bcoop.backup.blockfactory.BlockFactory;
import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.util.BitCoopFile;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class FileAssemblyTest extends TestCase {

    public final void testAssembleBlock() throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        try{
            final int TEST_SIZE = 1000+500; 
            BitCoopFile randomFile = AllTests.createTestFile(TEST_SIZE, null);
            BlockFactory factory = new BlockFactory(MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey(), randomFile.getAbsolutePath());
            factory.setMaxBlockSize(1000);

            Vector<DataBlock> blocks = new Vector<DataBlock>();
            while(factory.hasNextBlock()){
                blocks.add(factory.nextBlock());
            }
            HeaderBlock hBlock = factory.getHeaderBlock();
            assertNotNull(hBlock);
            assertNotNull(hBlock.getEncryptionIV());
            
            String tempDir = System.getProperty("user.dir")+"/testData/assemblyDir/";
            BlockAssembler assembly = new BlockAssembler(tempDir, hBlock, MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey());
            
            for(DataBlock block : blocks){
                assembly.assembleBlock(block);
            }
            
            BitCoopFile createdFile = new BitCoopFile(assembly.getAssembledPath());
            assertTrue(createdFile.exists());
            assertEquals(TEST_SIZE, createdFile.length());
            assertTrue(compareFiles(randomFile, createdFile));
        }
        catch(IOException e){
            e.printStackTrace();
            fail();
        }
    }

    /**
     * @param randomFile
     * @param createdFile
     * @return
     */
    private boolean compareFiles(BitCoopFile file1, BitCoopFile file2) {
        try{
            FileInputStream fis1 = new FileInputStream(file1);
            FileInputStream fis2 = new FileInputStream(file2);
            
            while(fis1.available()>0 && fis2.available()>0){
            	int i1 = fis1.read();
            	int i2 = fis2.read();
                if(i1 != i2){
                	System.out.println(i1+ " " + fis1.available());
                	System.out.println(i2+ " " + fis2.available());
                	return false;
                }
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

}

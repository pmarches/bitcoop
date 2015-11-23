/**
 * <p>Title: BlockFactoryTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.blockfactory;

import java.io.IOException;
import java.nio.ByteBuffer;

import mockObject.MockIdentityManager;

import bcoop.AllTests;
import bcoop.backup.blockfactory.BlockFactory;
import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.util.BitCoopFile;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class BlockFactoryTest extends TestCase {
	
	private static final String EMPTY_DIR_PATH = "testData/filesToBackup";

	public final void testBlockFactory() {
		try{
			final int TEST_SIZE = BlockFactory.DEFAULT_MAX_BLOCK_SIZE+500; 
			BitCoopFile testFile = AllTests.createTestFile(TEST_SIZE, null);
			assertEquals(TEST_SIZE, testFile.length());
			BlockFactory factory = new BlockFactory(MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey(), testFile.getAbsolutePath());
			factory.setMaxBlockSize(200000);
			
			assertTrue(factory.hasNextBlock());
			DataBlock block1 = factory.nextBlock();
			ByteBuffer blockData = ByteBuffer.wrap(block1.getBlockData());
			assertTrue(blockData.remaining() < TEST_SIZE);
						
			assertTrue(factory.hasNextBlock());
			DataBlock block2 = factory.nextBlock();
			blockData = ByteBuffer.wrap(block2.getBlockData());
			assertTrue(blockData.remaining() < TEST_SIZE);
			
			assertFalse(factory.hasNextBlock());
			HeaderBlock mBlock = factory.getHeaderBlock();
			assertNotNull(mBlock);
			mBlock.checkValidBlock(block1);
			mBlock.checkValidBlock(block2);
			
			assertEquals(0, mBlock.getMetaDataForDataBlock(block1.getBlockId()).getOffset());
			assertEquals(721777, mBlock.getMetaDataForDataBlock(block2.getBlockId()).getOffset());
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
	}
	
	public final void testFactoryOnDirectory() throws IOException{
		BlockFactory factory = new BlockFactory(MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey(), EMPTY_DIR_PATH);
		assertFalse(factory.hasNextBlock());
		HeaderBlock hBlock = factory.getHeaderBlock();
		assertEquals(HeaderBlock.FS_DIRECTORY_OBJECT, hBlock.getFSObjectType());
	}
	
}

/**
 * <p>Title: BlockRepositoryTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.repos;

import java.io.FileOutputStream;
import java.util.Vector;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.exception.StorageLimitException;
import bcoop.util.BitCoopFile;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class FSBlockRepositoryTest extends TestCase {
	private static final long DATA_BLOCKID = 0xA20584;
	private static final long HEADER_BLOCKID = 0xA20585;
	final String REPOS_DIR = "testData/blockRepository/bcoop/repos/2";
	DataBlock dblock1 = new DataBlock(DATA_BLOCKID, null);
	HeaderBlock hblock = new HeaderBlock(HEADER_BLOCKID, "someFile", 0);
	FSBlockRepository bRepos = null;
	
	public void setUp(){
		bRepos = new FSBlockRepository(REPOS_DIR);
		hblock.registerDataBlock(dblock1, 0, 0);
	}
	
	public void tearDown(){
		bRepos.destroyRepository();
	}
	
	public final void testGetFilePath(){
		assertEquals(REPOS_DIR+"/a3/c4/10", bRepos.getFilePath(0xA3C410));
		assertEquals(REPOS_DIR+"/00", bRepos.getFilePath(0x0));
		assertEquals(REPOS_DIR+"/0a/3c", bRepos.getFilePath(0xA3C));
	}
	
	public final void testGetFile(){
		try{
			BitCoopFile genFile = bRepos.getFileForId(0xA3C410);
			BitCoopFile expectedFile = new BitCoopFile(REPOS_DIR+"/a3/c4/10/data");
			assertEquals(expectedFile.getAbsolutePath(), genFile.getAbsolutePath());
			FileOutputStream expectedFileOutStream = new FileOutputStream(expectedFile);
			expectedFileOutStream.write(0);
			assertTrue(expectedFile.exists());
			assertTrue(expectedFile.isFile());
			assertTrue(expectedFile.canRead());
			assertTrue(expectedFile.canWrite());
			expectedFileOutStream.close();
			
			bRepos.getFileForId(0x0);
			expectedFile = new BitCoopFile(REPOS_DIR+"/00/data");
			expectedFileOutStream = new FileOutputStream(expectedFile);
			expectedFileOutStream.write(0);
			assertTrue(expectedFile.exists());
			assertTrue(expectedFile.isFile());
			assertTrue(expectedFile.canRead());
			assertTrue(expectedFile.canWrite());
			expectedFileOutStream.close();
			
			bRepos.getFileForId(0xA3C);
			expectedFile = new BitCoopFile(REPOS_DIR+"/0a/3c/data");
			expectedFileOutStream = new FileOutputStream(expectedFile);
			expectedFileOutStream.write(0);
			assertTrue(expectedFile.exists());
			assertTrue(expectedFile.isFile());
			assertTrue(expectedFile.canRead());
			assertTrue(expectedFile.canWrite());
			expectedFileOutStream.close();
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	public final void testDataBlock() {
		bRepos.storeBlock(dblock1);
		bRepos.saveIndex();
		BitCoopFile indexFile = new BitCoopFile(REPOS_DIR+"/headerIds");
		assertTrue(indexFile.exists());
		assertTrue(indexFile.length() > 0);
		
		DataBlock theBlock = (DataBlock) bRepos.getBlock(DATA_BLOCKID);
		assertNotNull(theBlock);
		assertTrue(theBlock.getBlockId() == dblock1.getBlockId());
		
		//Load it from disk.
		bRepos = new FSBlockRepository(REPOS_DIR);
		theBlock = (DataBlock) bRepos.getBlock(DATA_BLOCKID);
		assertNotNull(theBlock);
		assertTrue(theBlock.getBlockId() == dblock1.getBlockId());
		assertEquals(theBlock.getBlockData(), dblock1.getBlockData());
	}
	
	public final void testHeaderBlock() {
		try{
			bRepos.storeBlock(hblock);
			bRepos.saveIndex();
			BitCoopFile indexFile = new BitCoopFile(REPOS_DIR+"/headerIds");
			assertTrue(indexFile.exists());
			assertTrue(indexFile.length() > 0);
			
			HeaderBlock theBlock = (HeaderBlock) bRepos.getBlock(HEADER_BLOCKID);
			assertNotNull(theBlock);
			assertTrue(theBlock.getBlockId() == hblock.getBlockId());
			
			//Load it from disk.
			bRepos = new FSBlockRepository(REPOS_DIR);
			theBlock = (HeaderBlock) bRepos.getBlock(HEADER_BLOCKID);
			assertNotNull(theBlock);
			assertTrue(theBlock.getBlockId() == hblock.getBlockId());
			assertEquals(theBlock.getFilename(), hblock.getFilename());
			assertNotNull(theBlock.getAssociatedDataBlockId());
			assertEquals(this.dblock1.getBlockId(), theBlock.getAssociatedDataBlockId().getFirst().longValue());
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	public void testGetAllHeader(){
		bRepos.storeBlock(hblock);
		bRepos.saveIndex();
		Vector<Long> allHeaders = bRepos.getAllHeaderBlockId();
		assertNotNull(allHeaders);
		assertEquals(1, allHeaders.size());
		
		//Load it from disk.
		bRepos = new FSBlockRepository(REPOS_DIR);
		allHeaders = bRepos.getAllHeaderBlockId();
		assertNotNull(allHeaders);
		assertEquals(1, allHeaders.size());
	}
	
	public void testGetUsedSpace(){
		assertEquals(0, bRepos.getUsedSpace());
		bRepos.storeBlock(hblock);
		assertEquals(522, bRepos.getUsedSpace());
		bRepos.storeBlock(dblock1);
		assertEquals(642, bRepos.getUsedSpace());
		assertEquals(642, bRepos.getUsedSpace(new BitCoopFile(REPOS_DIR)));
	}
	
	public void testCanStore() throws StorageLimitException{
		assertEquals(0, bRepos.getUsedSpace());
		assertEquals(0, bRepos.getAllowedSpace());
		assertTrue(bRepos.canStoreBlockSize(100));
		assertFalse(bRepos.canStoreBlockSize(FSBlockRepository.ALLOWED_SPACE_DELTA+1));
		bRepos.setAllowedSpace(10);
		assertTrue(bRepos.canStoreBlockSize(FSBlockRepository.ALLOWED_SPACE_DELTA+1));
		assertFalse(bRepos.canStoreBlockSize(FSBlockRepository.ALLOWED_SPACE_DELTA+100));
	}
}

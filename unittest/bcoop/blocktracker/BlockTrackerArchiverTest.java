package bcoop.blocktracker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import mockObject.MockNetwork;

import bcoop.block.HeaderBlock;
import bcoop.exception.MissingConfigurationException;
import bcoop.util.Configuration;
import junit.framework.TestCase;

public class BlockTrackerArchiverTest extends TestCase {

	/*
	 * Test method for 'bcoop.blocktracker.BlockTrackerArchiver.saveBlockTracker(BlockTracker)'
	 */
	public void testLoadAndSaveBlockTracker() {
		try{
			BlockTracker tracker = new BlockTracker(2, new MockNetwork());
			HeaderBlock hBlock1 = new HeaderBlock(1234, "filename", 123);
			tracker.addHeaderBlock(hBlock1);
			
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			BlockTrackerArchiver.saveBlockTracker(tracker, oStream);
			ByteArrayInputStream iStream = new ByteArrayInputStream(oStream.toByteArray());
			BlockTracker tracker2 = BlockTrackerArchiver.loadTracker(2, new MockNetwork(), iStream);
			
			assertEquals(tracker2.getNbTrackedFiles(), tracker.getNbTrackedFiles());
			assertNotNull(tracker2.network);
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

	public void testGetTrackerFilename(){
		Configuration config = new Configuration();
		config.setProperty(Configuration.BASE_DIR, "/tmp");
		try {
			assertNotNull(BlockTrackerArchiver.getTrackerFilename(config));
		} catch (MissingConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		
	}
}

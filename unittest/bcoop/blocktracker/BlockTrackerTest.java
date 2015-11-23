/**
 * <p>Title: BlockTrackerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.blocktracker;

import java.security.PublicKey;
import java.util.Vector;
import java.util.LinkedList;

import mockObject.MockNetwork;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.block.MetaDataBlock;
import bcoop.block.TransactionBlock;
import bcoop.identity.Identity;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class BlockTrackerTest extends TestCase {
	private static final String FILENAME = "/tmp/foo";
	private static final Identity PEER1 = new Identity(0x1, "PEER1", (PublicKey) null);
	private static final Identity PEER2 = new Identity(0x2, "PEER2", (PublicKey) null);
	private static final String DATANAME1 = "dataName1";
	private static final String SCHEDULENAME1 = "schedule1";
	
	final TransactionBlock tBlock1 = new TransactionBlock(1, DATANAME1, SCHEDULENAME1);
	final TransactionBlock tBlock2 = new TransactionBlock(2, DATANAME1, SCHEDULENAME1);
	final HeaderBlock hBlock1 = new HeaderBlock(101, FILENAME, 0);
	final HeaderBlock hBlock2 = new HeaderBlock(102, FILENAME, 0);
	final DataBlock dBlock1 = new DataBlock(201, null);
	final DataBlock dBlock2 = new DataBlock(202, null);
	final DataBlock dBlock3 = new DataBlock(203, null);
	
	BlockTracker tracker = null;
	
	public void setUp(){
		tracker = new BlockTracker(1, new MockNetwork());
		hBlock1.registerDataBlock(dBlock1, 0, 0);
		hBlock1.registerDataBlock(dBlock2, 100, 0);
		
		hBlock2.registerDataBlock(dBlock2, 0, 0);
		hBlock2.registerDataBlock(dBlock3, 100, 0);

		tracker.addHeaderBlock(hBlock1);

		tBlock1.addHeaderBlockToTransaction(hBlock1);
		tracker.addTransaction(tBlock1);

		tBlock2.addHeaderBlockToTransaction(hBlock2);
		//We do not add the second transaction yet.
	}
	
	public final void testGetLatestBlock(){
		assertSame(tBlock1, tracker.getLatestTransactionBlock(DATANAME1));
		assertSame(hBlock1, tracker.getLatestHeaderBlockForFile(FILENAME));

		tracker.addHeaderBlock(hBlock2);
		tracker.addTransaction(tBlock2);
		assertSame(tBlock2, tracker.getLatestTransactionBlock(DATANAME1));
		assertSame(hBlock2, tracker.getLatestHeaderBlockForFile(FILENAME));
		
		Vector<Long> blocksOfT1 = tracker.getBlocksOnlyReferencedBy(tBlock1);
		assertEquals(2, blocksOfT1.size());
		assertEquals(hBlock1.getBlockId(), blocksOfT1.get(0).longValue());
		assertEquals(dBlock1.getBlockId(), blocksOfT1.get(1).longValue());
	}
	
	public final void testLog(){
		tracker.logBlockWasSentTo(dBlock1, PEER1);
		assertEquals(PEER1, tracker.getPeerListForBlock(dBlock1.getBlockId()).get(0));

		tracker.logBlockWasSentTo(dBlock1, PEER2);
		assertEquals(PEER1, tracker.getPeerListForBlock(dBlock1.getBlockId()).get(0));
		assertEquals(PEER2, tracker.getPeerListForBlock(dBlock1.getBlockId()).get(1));
	}
		
	public final void testGetBlockListForFile() {
		HeaderBlock hBlock = tracker.getLatestHeaderBlockForFile(FILENAME);
		assertNotNull(hBlock);
		assertEquals(hBlock1.getBlockId(), hBlock.getBlockId());
		
		LinkedList<Long> blocksIds = hBlock.getAssociatedDataBlockId();
		assertEquals(2, blocksIds.size());
		assertEquals(dBlock1.getBlockId(), blocksIds.getLast().longValue());
		assertEquals(dBlock2.getBlockId(), blocksIds.getFirst().longValue());
		MetaDataBlock metaBlock = hBlock.getMetaDataForDataBlock(blocksIds.getFirst());
	}
	
	public final void testGetDeleteableBlocks(){
		//HeaderBlock hBlock =
		TransactionBlock latestTransBlock = tracker.getLatestTransactionBlock(DATANAME1);
		assertEquals(tBlock1.getBlockId(), latestTransBlock.getBlockId());

		tracker.addTransaction(tBlock2);
		latestTransBlock = tracker.getLatestTransactionBlock(DATANAME1);
		assertEquals(tBlock2.getBlockId(), latestTransBlock.getBlockId());
		
		Vector<Long> blocksToDelete = tracker.getBlocksOnlyReferencedBy(tBlock1);
		assertEquals(2, blocksToDelete.size());
		assertEquals(hBlock1.getBlockId(), blocksToDelete.get(0).longValue());
		assertEquals(dBlock1.getBlockId(), blocksToDelete.get(1).longValue());
	}
	
	public final void testgetAllTransactionBlock(){
		Vector<TransactionBlock> tBlocks = tracker.getAllTransactionBlock();
		assertEquals(1, tBlocks.size());
		assertSame(tBlock1, tBlocks.get(0));
	}
}

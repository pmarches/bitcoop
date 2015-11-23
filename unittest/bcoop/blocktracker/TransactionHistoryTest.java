package bcoop.blocktracker;

import java.util.Vector;

import bcoop.block.TransactionBlock;
import bcoop.blocktracker.history.DiscardEventHandlerIf;
import bcoop.blocktracker.history.TransactionHistory;
import junit.framework.TestCase;

public class TransactionHistoryTest extends TestCase {
	private static final String DATANAME1 = "Job1";
	private static final String SCHEDULE1 = "schedule1";
	TransactionBlock transaction1 = new TransactionBlock(1, DATANAME1, SCHEDULE1);
	TransactionBlock transaction2 = new TransactionBlock(2, DATANAME1, SCHEDULE1);
	TransactionBlock transaction3 = new TransactionBlock(3, DATANAME1, SCHEDULE1);
	TransactionBlock transaction4 = new TransactionBlock(4, DATANAME1, SCHEDULE1);

	class MockDiscardEventHandler implements DiscardEventHandlerIf<TransactionBlock>{
		Vector<TransactionBlock> blocksRemoved = new Vector<TransactionBlock>();
		public TransactionHistory associatedHistory;

		public void onBeforeBlockToRemove(TransactionBlock blockToRemove) {
			if(associatedHistory != null){
				assertTrue(associatedHistory.getNumberOfRevisions() <= associatedHistory.getMaxNumberOfRevision()+1);
			}
			blocksRemoved.add((TransactionBlock) blockToRemove);
		}
	}

	/*
	 * Test method for 'bcoop.blocktracker.TransactionHistory.getLastTransaction()'
	 */
	public void testGetLastTransaction() {
		MockDiscardEventHandler discardHandler = new MockDiscardEventHandler();
		TransactionHistory tHistory = new TransactionHistory(3, discardHandler);
		discardHandler.associatedHistory = tHistory;
		
		tHistory.addNewest(transaction1);
		assertSame(transaction1, tHistory.getNewest());

		tHistory.addNewest(transaction2);
		assertSame(transaction2, tHistory.getNewest());
		
		assertEquals(0, discardHandler.blocksRemoved.size());
	}

	/*
	 * Test method for 'bcoop.blocktracker.TransactionHistory.addLatestTransaction(TransactionBlock)'
	 */
	public void testAddLatestTransaction() {
		MockDiscardEventHandler discardHandler = new MockDiscardEventHandler();
		TransactionHistory tHistory = new TransactionHistory(2, discardHandler);
		discardHandler.associatedHistory = tHistory;
		
		tHistory.addNewest(transaction1);
		assertEquals(1, tHistory.getNumberOfRevisions());
		assertEquals(0, discardHandler.blocksRemoved.size());

		tHistory.addNewest(transaction2);
		assertEquals(2, tHistory.getNumberOfRevisions());
		assertEquals(0, discardHandler.blocksRemoved.size());

		tHistory.addNewest(transaction3);
		assertEquals(2, tHistory.getNumberOfRevisions());
		assertEquals(1, discardHandler.blocksRemoved.size());
		assertSame(transaction1, discardHandler.blocksRemoved.get(0));

		tHistory.addNewest(transaction4);
		assertEquals(2, tHistory.getNumberOfRevisions());
		assertEquals(2, discardHandler.blocksRemoved.size());
		assertSame(transaction2, discardHandler.blocksRemoved.get(1));
	}

}

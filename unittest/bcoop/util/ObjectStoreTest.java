package bcoop.util;

import java.io.IOException;

import bcoop.AllTests;
import bcoop.block.DataBlock;
import bcoop.block.MetaDataBlock;
import bcoop.blocktracker.history.TransactionHistory;
import bcoop.exception.NoLocalIdentityDefinedException;
import bcoop.identity.Identity;
import mockObject.MockIdentityManager;
import mockObject.MockObjectStore;
import junit.framework.TestCase;

public class ObjectStoreTest extends TestCase {
	ObjectStore oStore;
	
	protected void setUp() throws Exception {
		AllTests.setupLogging();
		oStore = new MockObjectStore();
	}
	
	protected void tearDown(){
		oStore.close();
	}

	public void testDataBlockStore() {
		DataBlock dBlock = new DataBlock();
		oStore.save(dBlock);
		assertEquals(1, dBlock.getBlockId());
		DataBlock dBlockRead = (DataBlock) oStore.getBlock(dBlock.getBlockId());
		assertSame(dBlock, dBlockRead);
		
		MetaDataBlock metaDataBlock = new MetaDataBlock(1, 1, 1);
		oStore.save(metaDataBlock);
		assertEquals(2, metaDataBlock.getBlockId());
	}
	
	public void xtestHistoryStore(){
		TransactionHistory tHistory = new TransactionHistory(3, null);
		oStore.save(tHistory);
	}
	
	public void testIdentity(){
		try {
			MockIdentityManager mockIdentityManager = new MockIdentityManager(oStore);
			Identity ident = mockIdentityManager.getLocalIdentity();
			oStore.save(ident);
			Identity identityRead = oStore.getIdentity(ident.getUniqueId());
			assertEquals(ident, identityRead);
		} catch (Exception e) {
		}

	}

}

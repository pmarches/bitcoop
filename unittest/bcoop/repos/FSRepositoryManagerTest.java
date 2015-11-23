/**
 * <p>Title: RepositoryManagerTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.repos;

import java.security.PublicKey;

import bcoop.block.DataBlock;
import bcoop.block.NumberedBlock;
import bcoop.exception.MissingConfigurationException;
import bcoop.exception.StorageLimitException;
import bcoop.identity.Identity;
import bcoop.util.Configuration;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class FSRepositoryManagerTest extends TestCase {
	FSRepositoryManager repoMan = null;
	final String TEMP_DIRECTORY = "/tmp/bcoopTest";
	
    public void setUp() throws StorageLimitException{
    		try{
	    		Configuration config = new Configuration();
	    		config.setProperty(Configuration.DEFAULT_ALLOWED_PER_PEER, "UNLIMITED");
	    		config.setProperty(Configuration.GLOBAL_ALLOWED_SPACE, "UNLIMITED");
	    		config.setProperty(Configuration.BASE_DIR, TEMP_DIRECTORY);
	    		
	        repoMan =  new FSRepositoryManager(config);
    		}
    		catch(MissingConfigurationException e){
    			e.printStackTrace();
    			fail();
    		}
    }
    
    public void tearDown(){
        repoMan.destroyAllRepositories();
    }

	public final void testStoreBlock() throws StorageLimitException {
		DataBlock block = new DataBlock(596982, null);
		Identity identity = new Identity(0x1, "1", (PublicKey) null);
		repoMan.storeBlock(identity, block);
		NumberedBlock gotBlot = repoMan.getBlock(identity, block.getBlockId());
		assertEquals(gotBlot.getBlockId(), block.getBlockId());
	}

	public final void testCloseRepositories() {
		repoMan.destroyAllRepositories();
	}

	public final void testGetRepositoryForPeer() {
		Identity peerThatDoesNotExist = new Identity(0x11234, "asd", (PublicKey) null);
		assertNotNull(repoMan.getRepositoryForPeer(peerThatDoesNotExist));
	}
	
	public void testCanStoreBlockSizeInGlobalAllowedSpace(){
		assertEquals(RepositoryManager.UNLIMITED_SPACE_ALLOWED, repoMan.getGlobalMaximumAllowedSpace());
		
		assertTrue(repoMan.canStoreBlockSizeInGlobalAllowedSpace(1000000));
		repoMan.setGlobalMaximumAllowedSpace(100);
		assertTrue(repoMan.canStoreBlockSizeInGlobalAllowedSpace(100));
		assertFalse(repoMan.canStoreBlockSizeInGlobalAllowedSpace(101));
	}
	
	public void testCanStoreBlockSizePerPeer() throws StorageLimitException{
		final Identity PEER1 = new Identity(0x1, "peer1", (PublicKey) null);
		assertEquals(RepositoryManager.UNLIMITED_SPACE_ALLOWED, repoMan.getGlobalMaximumAllowedSpace());

		assertTrue(repoMan.canStoreBlockSizeInLocalRepository(PEER1, 1000000));
		repoMan.getRepositoryForPeer(PEER1).setAllowedSpace(100);
		assertTrue(repoMan.canStoreBlockSizeInLocalRepository(PEER1, BlockRepository.ALLOWED_SPACE_DELTA+100));
		assertFalse(repoMan.canStoreBlockSizeInLocalRepository(PEER1, BlockRepository.ALLOWED_SPACE_DELTA+101));

		repoMan.getRepositoryForPeer(PEER1).setAllowedSpace(BlockRepository.UNLIMITED_SPACE_ALLOWED);
		assertTrue(repoMan.canStoreBlockSizeInLocalRepository(PEER1, BlockRepository.ALLOWED_SPACE_DELTA+101));
	}

}

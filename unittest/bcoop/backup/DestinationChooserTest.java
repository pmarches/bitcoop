package bcoop.backup;

import java.security.PublicKey;

import mockObject.MockFile;
import mockObject.MockNetwork;
import mockObject.MockRepositoryManager;
import bcoop.block.DataBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.exception.NoPeerAvailableException;
import bcoop.exception.StorageLimitException;
import bcoop.identity.Identity;
import bcoop.network.PeerInformation;
import bcoop.network.PeerManager;
import bcoop.repos.RepositoryManager;
import bcoop.util.BitCoopFile;
import junit.framework.TestCase;

public class DestinationChooserTest extends TestCase {
	private static final int SIZE_FILE2 = 1000;
	private static final int SIZE_FILE1 = 2000;
	DestinationChooser dChooser;
	PeerManager peerMan;
	BlockTracker bTracker;
	RepositoryManager repoMan;
	final Identity PEER1 = new Identity(0x1, "peer1", (PublicKey) null);
	final Identity PEER2 = new Identity(0x2, "peer2", (PublicKey) null);
	
	public void setUp(){
		peerMan = new PeerManager();
		bTracker = new BlockTracker(2, new MockNetwork());
		repoMan = new MockRepositoryManager();
		repoMan.increaseAllowedSpaceForPeer(PEER1, SIZE_FILE1);
		repoMan.increaseAllowedSpaceForPeer(PEER2, SIZE_FILE2);
	}
	
	public void testGetDestination() throws StorageLimitException{
		dChooser = new DestinationChooser(peerMan, bTracker, repoMan);
		try{
			dChooser.getDestinationForCompleteFile((BitCoopFile)null);
			fail();
		}
		catch(NoPeerAvailableException npa){
			assertTrue(true); //Normal, no peers in peermanager.
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		try {
			peerMan.addPeer(new PeerInformation(PEER1));
			final BitCoopFile file1 = new MockFile("/toto/file1", SIZE_FILE1, 0);
			assertEquals(PEER1, dChooser.getDestinationForCompleteFile(file1));
			repoMan.storeBlock(PEER1, new DataBlock(1, new byte[SIZE_FILE1]));
			
			peerMan.addPeer(new PeerInformation(PEER2));
			final BitCoopFile file2 = new MockFile("/toto/file2", SIZE_FILE2, 0);
			assertEquals(PEER2, dChooser.getDestinationForCompleteFile(file2));

		} catch (NoPeerAvailableException e) {
			e.printStackTrace();
			fail();
		}
	}
}

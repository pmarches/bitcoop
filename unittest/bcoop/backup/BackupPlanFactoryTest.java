/**
 * <p>Title: BackupPlanfactoryTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup;

import java.security.PublicKey;

import mockObject.MockFile;
import mockObject.MockNetwork;
import bcoop.block.HeaderBlock;
import bcoop.blocktracker.BlockTracker;
import bcoop.identity.Identity;
import bcoop.network.PeerInformation;
import bcoop.network.PeerManager;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class BackupPlanFactoryTest extends TestCase {
	BackupPlanfactory factory;
    private BlockTracker bTracker;
    private PeerManager peerMan;
    final Identity DEFAULT_PEER = new Identity(0x1, "somePeer", (PublicKey) null); 
    
    public void setUp(){
        bTracker = new BlockTracker(2, new MockNetwork());
        peerMan = new PeerManager();
        peerMan.addPeer(new PeerInformation(DEFAULT_PEER));
        factory = new BackupPlanfactory(bTracker);
    }

    public final void testRequiresBackup() {
        try{
            final String FILENAME1 = "/tmp/toto";
            final String FILENAME2 = "/tmp/toto2";
            long now = 10;
    
            MockFile file1 = new MockFile(FILENAME1, 0, now);
            BackupPlan plan = factory.createBackupPlan(file1);
            assertTrue(plan.requiresBackup());
            
            bTracker.addHeaderBlock(new HeaderBlock(0x120, file1.getAbsolutePath(), now+1));
            plan = factory.createBackupPlan(file1);
            assertFalse(plan.requiresBackup());
            
            file1.modificationTime = now+100;
            plan = factory.createBackupPlan(file1);
            assertTrue(plan.requiresBackup());
    
            MockFile file2 = new MockFile(FILENAME2, 0, now);
            plan = factory.createBackupPlan(file2);
            assertTrue(plan.requiresBackup());
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

}

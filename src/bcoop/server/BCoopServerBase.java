/**
 * <p>Title: BCoopServerIF.java</p>
 * <p>Description: Basic interface to enable mock Server</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.server;

import bcoop.backup.scheduler.JobScheduler;
import bcoop.blocktracker.BlockTracker;
import bcoop.identity.IdentityManager;
import bcoop.network.Network;
import bcoop.network.PeerInformation;
import bcoop.repos.RepositoryManager;
import bcoop.worker.BackupWorker;
import bcoop.worker.RestoreWorker;

/**
 * @author pmarches
 *
 */
public class BCoopServerBase {
    public PeerInformation ourPeer;
    public BackupWorker backupWorker;
    public RestoreWorker restoreWorker;
    public RepositoryManager reposManager;
    public BlockTracker blockTracker;
    public Network network;
    public JobScheduler jobScheduler;
    public IdentityManager identityManager;

    /**
     * @return
     */
    public RepositoryManager getRepositoryManager() {
        return reposManager;
    }

    /**
     * @return
     */
    public Network getNetwork() {
        return network;
    }

    /**
     * @return
     */
    public BlockTracker getBlockTracker() {
        return blockTracker;
    }
    

}

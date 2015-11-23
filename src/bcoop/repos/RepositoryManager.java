/**
 * <p>Title: RepositoryList.java</p>
 * <p>Description: Base repository management facility</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.repos;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import bcoop.block.NumberedBlock;
import bcoop.exception.StorageLimitException;
import bcoop.identity.Identity;
import bcoop.network.BlockReceiverHandler;

/**
 * @author pmarches
 *
 */
public abstract class RepositoryManager implements BlockReceiverHandler{
    public final static long UNLIMITED_SPACE_ALLOWED=-1;
    public final static long NO_SPACE_ALLOWED=0;

    Hashtable<Identity, BlockRepository> peerRepositories = new Hashtable<Identity, BlockRepository>();
    private long maximumAllowedSpace = NO_SPACE_ALLOWED;
    Hashtable<Identity, Long> spaceUsedRemotely = new Hashtable<Identity, Long>();

    protected abstract BlockRepository createBlockRepository(Identity peerId);
    public abstract void closeAllRepository();

	/**
	 * @param newBlock
	 * @return 
	 * @throws StorageLimitException 
	 */
	public long storeBlock(Identity sourcePeerId, NumberedBlock newBlock) throws StorageLimitException {
		if(canStoreBlockSizeInGlobalAllowedSpace(newBlock.getStorageSizeOfBlock()) == false){
            throw new StorageLimitException("Global storage limit reached.");
		}
		BlockRepository repos = getRepositoryForPeer(sourcePeerId);
        if(repos.canStoreBlockSize(newBlock.getStorageSizeOfBlock()) == false){
            throw new StorageLimitException("No more space available for peer "+sourcePeerId+". Use canStoreBlock() to check availability.");
        }
		repos.storeBlock(newBlock);
		return repos.getUsedSpace();
	}
	
	public boolean canStoreBlockSizeInGlobalAllowedSpace(long blockSize){
		if(maximumAllowedSpace == UNLIMITED_SPACE_ALLOWED){
			return true;
		}
		if(getLocalSpaceUsedByAllPeers()+blockSize <= maximumAllowedSpace){
			return true;
		}
		return false;
	}
    
	public boolean canStoreBlockSizeRemotely(Identity destinationPeer, long blockSize){
		Long spaceUsedOnRemotePeer = spaceUsedRemotely.get(destinationPeer);
		if(spaceUsedOnRemotePeer == null){
			spaceUsedOnRemotePeer = new Long(0);
		}
		long spaceWeHostForRemotePeer = this.getRepositoryForPeer(destinationPeer).getUsedSpace();
		return spaceUsedOnRemotePeer+blockSize < spaceWeHostForRemotePeer + BlockRepository.ALLOWED_SPACE_DELTA;
	}
	
    public boolean canStoreBlockSizeInLocalRepository(Identity sourcePeerId, long blockSize) {
        BlockRepository repos = getRepositoryForPeer(sourcePeerId);
        return repos.canStoreBlockSize(blockSize);
    }

	/**
	 * @param receiverId
	 * @return
	 */
	synchronized public BlockRepository getRepositoryForPeer(Identity peerId) {
		BlockRepository repo = peerRepositories.get(peerId);
		if(repo == null){
			Logger.getLogger(this.getClass()).info("Creating repository "+ peerId);
            repo = createBlockRepository(peerId);
			registerRepository(peerId, repo);
		}
		return repo;
	}


    /**
	 * @param peerName
	 */
	synchronized protected void registerRepository(Identity peerId, BlockRepository repos) {
		peerRepositories.put(peerId, repos);
		Logger.getLogger(this.getClass()).info("Registered repository "+peerId);
	}

	/**
	 * @param string
	 * @param blockId
	 * @return
	 */
	public NumberedBlock getBlock(Identity peerId, long blockId) {
		BlockRepository repo = getRepositoryForPeer(peerId);
		return repo.getBlock(blockId);
	}

	/* (non-Javadoc)
	 * @see bcoop.network.BlockReceiverHandler#handleReceivedBlock(bcoop.block.NumberedBlock)
	 */
	public long handleReceivedBlock(Identity sourcePeerId, NumberedBlock newBlock) throws StorageLimitException {
		return this.storeBlock(sourcePeerId, newBlock);
	}
    
    public void destroyAllRepositories(){
        for(BlockRepository repo : this.peerRepositories.values()){
            repo.destroyRepository();
        }
    }
    
    public long getLocalSpaceUsedByAllPeers(){
        long total = 0;
        for(BlockRepository repo : this.peerRepositories.values()){
            total+=repo.getUsedSpace();
        }
        return total;
    }

    public long getTotalAllowedSpaceForAllPeers(){
        long total = 0;
        for(BlockRepository repo : this.peerRepositories.values()){
            total+=repo.getAllowedSpace();
        }
        return total;
    }
    
    public void increaseAllowedSpaceForPeer(Identity peerId, long size) {
        BlockRepository repo = getRepositoryForPeer(peerId);
        repo.increaseAllowedSpace(size);
    }

    public void decreaseAllowedSpaceForPeer(Identity peerId, long size) {
        BlockRepository repo = getRepositoryForPeer(peerId);
        repo.decreaseAllowedSpace(size);
    }
    
    public long getGlobalMaximumAllowedSpace() {
        return maximumAllowedSpace;
    }
    
    public void setGlobalMaximumAllowedSpace(long maximumAllowedSpace) {
        this.maximumAllowedSpace = maximumAllowedSpace;
    }
	public boolean isRepositoryExist(String peerId) {
		return this.peerRepositories.containsKey(peerId);
	}

	public boolean hasGlobalMaximumAllowedSpace() {
		return this.maximumAllowedSpace != UNLIMITED_SPACE_ALLOWED;
	}
}

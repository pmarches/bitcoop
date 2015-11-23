/**
 * <p>Title: BlockTracker.java</p>
 * <p>Description: Keep track of which block has been sent to wch server and so on..</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.blocktracker;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Vector;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import bcoop.block.HeaderBlock;
import bcoop.block.NumberedBlock;
import bcoop.block.TransactionBlock;
import bcoop.blocktracker.history.DiscardEventHandlerIf;
import bcoop.blocktracker.history.HeaderBlockHistory;
import bcoop.blocktracker.history.TransactionHistory;
import bcoop.exception.ConnectionRefusedException;
import bcoop.exception.StreamClosedException;
import bcoop.identity.Identity;
import bcoop.network.Connection;
import bcoop.network.Network;
import bcoop.util.HashOfArray;

/**
 * @author pmarches
 *
 */
public class BlockTracker implements Externalizable, DiscardEventHandlerIf {
	private static final long serialVersionUID = -5983291330950774118L;
	Hashtable<String, TransactionHistory> dataGroupTransactions = new Hashtable<String, TransactionHistory>();
	Hashtable<String, HeaderBlockHistory> headerHistory = new Hashtable<String, HeaderBlockHistory>();
	Hashtable<Identity, Long> peerBytesSent = new Hashtable<Identity, Long>();
	BlockToPeerTracking allBlockToPeer;
	private int transactionHistorySize;
	transient protected Network network;
	
	public BlockTracker(){
		//Serialization constructor
	}
	
	public BlockTracker(int transactionHistorySize, Network network){
		if(transactionHistorySize <= 0){
			throw new RuntimeException("Invalid transaction history size, must be > 0");
		}
		this.transactionHistorySize = transactionHistorySize;
		this.network = network;
		this.allBlockToPeer = new BlockToPeerTracking();
	}
	
	public Vector<String> getJobsWithFile(String filename){
		Vector<String> jobsWithThefile = new Vector<String>();
		for(TransactionHistory tHistory : dataGroupTransactions.values()){
			TransactionBlock lastTransaction = tHistory.getNewest();
			if(lastTransaction.getHeaderBlockForFile(filename) != null){
				jobsWithThefile.add(lastTransaction.getDataName());
			}
		}
		return jobsWithThefile;
	}
	
	public HeaderBlock getLatestHeaderBlockForFile(String filename) {
		HeaderBlockHistory hHistory = headerHistory.get(filename);
		if(hHistory==null){
			return null;
		}
		return hHistory.getNewest();
	}

    public void addTransaction(TransactionBlock tBlock) {
    		TransactionHistory tHistory = dataGroupTransactions.get(tBlock.getDataName());
    		if(tHistory == null){
			Logger.getLogger(this.getClass()).debug("This is the first transaction for datagroup "+tBlock.getDataName());
    			tHistory = new TransactionHistory(this.transactionHistorySize, this);
    			dataGroupTransactions.put(tBlock.getDataName(), tHistory);
    		}
    		tHistory.addNewest(tBlock);
    }

    public void addHeaderBlock(HeaderBlock hBlock) {
    		HeaderBlockHistory hbHistory = this.headerHistory.get(hBlock.getFilename());
    		if(hbHistory == null){
    			hbHistory = new HeaderBlockHistory(this.transactionHistorySize, this);
    			this.headerHistory.put(hBlock.getFilename(), hbHistory);
    		}
    		hbHistory.addNewest(hBlock);
    }

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.allBlockToPeer);
		out.writeObject(this.headerHistory);
		out.writeObject(this.dataGroupTransactions);
		out.writeObject(this.peerBytesSent);
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.allBlockToPeer = (BlockToPeerTracking) in.readObject();
		this.headerHistory = (Hashtable<String, HeaderBlockHistory>) in.readObject();
		this.dataGroupTransactions = (Hashtable<String, TransactionHistory>) in.readObject();
		this.peerBytesSent =  (Hashtable<Identity, Long>) in.readObject();
	}

	public long getBytesStoredOn(Identity peer) {
		if(this.peerBytesSent.containsKey(peer)==false){
			return 0;
		}
		return this.peerBytesSent.get(peer);
	}
	public void setBytesStoredOnPeer(Identity peerId, long bytesSent){
		peerBytesSent.put(peerId, bytesSent);
	}

	public Vector<Long> getBlocksOnlyReferencedBy(TransactionBlock transaction) {
		Vector<Long> blockCandidateForDeletion = new Vector<Long>();
		transaction.getReferencedBlock(blockCandidateForDeletion);

		//Find out all the Blocks that are not referenced by looping on each block that
		//is referenced by this transaction.
		TransactionHistory tHistory = dataGroupTransactions.get(transaction.getDataName());
		Vector<Long> blockIdStillReferenced = new Vector<Long>();
		tHistory.getReferencedBlock(blockIdStillReferenced);
		Collections.sort(blockIdStillReferenced);
		Iterator it = blockCandidateForDeletion.iterator();
		while(it.hasNext()){
			Long blockCandidate = (Long) it.next();
			if(Collections.binarySearch(blockIdStillReferenced, blockCandidate) >= 0){
				//The block is still referenced! We remove it from the candidate list
				it.remove();
				continue;
			}
		}
		return blockCandidateForDeletion;
	}

	public TransactionBlock getLatestTransactionBlock(String dataGroupName) {
		TransactionHistory tHistory = dataGroupTransactions.get(dataGroupName);
		if(tHistory == null){
			Logger.getLogger(this.getClass()).info("No dataGroup named "+dataGroupName+" in the block tracker");
			return null;
		}
		return tHistory.getNewest();
	}

	public void logBlockWasSentTo(NumberedBlock block, Identity blockDestination) {
		this.allBlockToPeer.associateBlockToPeer(block.getBlockId(), blockDestination);
	}
	
	public void onBeforeBlockToRemove(Object blockToRemove) {
		if(blockToRemove instanceof HeaderBlock){
			onHeaderBlockToRemove((HeaderBlock) blockToRemove);
		}
		else if(blockToRemove instanceof TransactionBlock){
			onTransactionBlockToRemove((TransactionBlock) blockToRemove);
		}
		else{
			Logger.getLogger(this.getClass()).error("Got an unexpected block to remove:" +blockToRemove.getClass().toString());
		}
	}
	
	private void onHeaderBlockToRemove(HeaderBlock hBlock) {
		Logger.getLogger(this.getClass()).debug("About to invalidate header block "+hBlock.getBlockId());
		LinkedList<Long> blocksReadyForDeletion = hBlock.getAssociatedDataBlockId();
		invalidateBlocks(blocksReadyForDeletion);
	}

	public void onTransactionBlockToRemove(TransactionBlock transactionRemoved) {
		Logger.getLogger(this.getClass()).debug("About to invalidate transaction "+Long.toHexString(transactionRemoved.getBlockId()));
		Vector<Long> blocksReadyForDeletion = getBlocksOnlyReferencedBy(transactionRemoved);
		
		//TODO: for each Headerblock contained in the Transaction, invalidate the blocks that are present
		//in the blocksReadyForDeletion list.
		blocksReadyForDeletion.add(transactionRemoved.getBlockId());
		invalidateBlocks(blocksReadyForDeletion);
	}

	public void invalidateBlocks(Collection<Long> blocksToInvalidate) {
		HashOfArray<Identity, Long> peersAndBlocks = new HashOfArray<Identity, Long>();
		for(long blockId : blocksToInvalidate){
			Vector<Identity> peers = getPeerListForBlock(blockId);
			if(peers != null){
				for(Identity peerId : peers){
					peersAndBlocks.addValue(peerId, blockId);
				}
			}
		}
		Logger.getLogger(this.getClass()).debug("Will invalidate "+blocksToInvalidate.size()+" blocks distributed on "+peersAndBlocks.getNumberOfKeys()+" peers.");
		
		for(Identity peerId : peersAndBlocks.keySet()){
			try {
				invalidateBlocksOnPeer(peersAndBlocks.getAllBlockOfPeer(peerId), peerId);
			} catch (IOException e) {
				Logger.getLogger(this.getClass()).warn("Could not invalidate block on host "+peerId);
				//FIXME: Send invalidation later??
			} catch (ConnectionRefusedException cre) {
				Logger.getLogger(this.getClass()).warn("Could not invalidate block on host "+peerId);
				//FIXME: Send invalidation later??
			}
		}
		Logger.getLogger(this.getClass()).debug("Done invalidating blocks");
		
	}
	
	public Vector<Identity> getPeerListForBlock(long blockId) {
		Vector<Identity> peersThatHaveReceivedBlock = this.allBlockToPeer.getPeerListForBlock(blockId);
		if(peersThatHaveReceivedBlock == null){
			peersThatHaveReceivedBlock = new Vector<Identity>();
		}
		return peersThatHaveReceivedBlock;
	}

	private void invalidateBlocksOnPeer(Vector<Long> allBlockIdToInvalidate, Identity peerId) throws IOException, ConnectionRefusedException, StreamClosedException{
		Logger.getLogger(this.getClass()).debug("Invalidating "+allBlockIdToInvalidate.size()+" block on peer "+peerId);
		Connection con = this.network.getConnection(peerId);
		for(long blockId : allBlockIdToInvalidate){
			con.invalidateBlockId(blockId);
		}
		con.close();
	}

	public int getNbTrackedFiles() {
		return this.headerHistory.size();
	}

	public int getNbTrackedBlocks() {
		return this.allBlockToPeer.getNumberOfDifferentBlocks();
	}

	public Vector<TransactionBlock> getAllTransactionBlock() {
		Vector<TransactionBlock> allTransaction = new Vector<TransactionBlock>();
		for(TransactionHistory tHistory : this.dataGroupTransactions.values()){
			allTransaction.addAll(tHistory.getAllHistory());
		}

		return allTransaction;
	}

	public TransactionBlock getTransactionBlock(long transactionId) {
		// TODO re-arrange the transactions?
		for(TransactionHistory tHistory : this.dataGroupTransactions.values()){
			TransactionBlock tBlock = tHistory.getTransaction(transactionId);
			if(tBlock!=null) return tBlock;
		}
		return null;
	}

	public void setTransactionHistorysize(int transactionHistorySize) {
		this.transactionHistorySize = transactionHistorySize;
	}
}

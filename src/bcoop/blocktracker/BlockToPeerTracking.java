package bcoop.blocktracker;

import java.io.Serializable;
import java.util.Vector;

import javax.persistence.Entity;

import bcoop.identity.Identity;
import bcoop.util.HashOfArray;

@Entity
public class BlockToPeerTracking implements Serializable{
	private static final long serialVersionUID = -726953039035467119L;

	HashOfArray<Long, Identity> blockToPeer = new HashOfArray<Long, Identity>();
	
	public void associateBlockToPeer(Long blockId, Identity peerId){
		this.blockToPeer.addValue(blockId, peerId);
	}
	
	public Vector<Identity> getPeerListForBlock(Long blockId){
		return this.blockToPeer.getAllBlockOfPeer(blockId);
	}

	public int getNumberOfDifferentBlocks() {
		return blockToPeer.getNumberOfKeys();
	}
}

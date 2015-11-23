package bcoop.blocktracker.history;


import java.util.Vector;

import bcoop.block.TransactionBlock;

public class TransactionHistory extends BaseHistory<TransactionBlock>{
	private static final long serialVersionUID = -4768029284066590844L;

	public TransactionHistory(int maxHistory, DiscardEventHandlerIf<TransactionBlock> discardHandler){
		super(maxHistory, discardHandler);
	}
	
	public void getReferencedBlock(Vector<Long> blockReference) {
		for(TransactionBlock currentTBlock : this.revisions){
			currentTBlock.getReferencedBlock(blockReference);
		}
	}
	
	public TransactionBlock getTransaction(long transactionId) {
		for(TransactionBlock currentTBlock : this.revisions){
			if(transactionId == currentTBlock.getBlockId()){
				return currentTBlock;
			}
		}
		return null;
	}
}

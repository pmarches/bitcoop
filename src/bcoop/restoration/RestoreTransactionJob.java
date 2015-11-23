package bcoop.restoration;

import bcoop.block.HeaderBlock;
import bcoop.block.TransactionBlock;

public class RestoreTransactionJob extends RestoreJob {

	public RestoreTransactionJob(TransactionBlock tBlockToRestore, String restoreDir) {
		super(restoreDir);
		for(HeaderBlock hBlock : tBlockToRestore.getAllHeaderBlock()){
			this.addHeaderBlockToRestore(hBlock);
		}
	}
}

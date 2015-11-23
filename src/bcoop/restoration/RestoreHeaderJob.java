package bcoop.restoration;

import bcoop.block.HeaderBlock;

public class RestoreHeaderJob extends RestoreJob {
    public RestoreHeaderJob(HeaderBlock headerBlock, String restoreDir){
    		super(restoreDir);
        this.addHeaderBlockToRestore(headerBlock);
    }
}

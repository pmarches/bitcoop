package mockObject;

import java.util.Vector;

import bcoop.block.NumberedBlock;
import bcoop.identity.Identity;
import bcoop.network.BlockReceiverHandler;

public class MockBlockReceiverHandler implements BlockReceiverHandler{
	public int nbCall=0;
	public Vector<NumberedBlock> receivedBlock = new Vector<NumberedBlock>();
    public Identity remotePeerId;

	/* (non-Javadoc)
	 * @see bcoop.network.BlockReceiverHandler#handleReceivedBlock(bcoop.block.NumberedBlock)
	 */
	synchronized public long handleReceivedBlock(Identity remotePeerId, NumberedBlock newBlock) {
		this.nbCall++;
		this.receivedBlock.add(newBlock);
        this.remotePeerId = remotePeerId;
		notifyAll();
		return 0;
	}

	/**
	 * 
	 */
	synchronized public void waitForBlock(int nbBlocks) {
		while(receivedBlock.size() < nbBlocks){
			try {
				wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

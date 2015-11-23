/**
 * <p>Title: BlockTransferProtocolTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.security.PublicKey;
import java.util.Arrays;

import mockObject.MockBlockReceiverHandler;

import bcoop.block.DataBlock;
import bcoop.identity.Identity;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class BlockTransferProtocolTest extends TestCase {
	DataBlock blockToSend = new DataBlock(1234, "data".getBytes()); 
	final Identity remotePeerId = new Identity(0x1, "somePeer", (PublicKey) null);
    
	public void testBlockTransferProtocol(){
		try{
			PipeStream loopBack1 = new PipeStream();
			PipeStream loopBack2 = new PipeStream();
			
			MockBlockReceiverHandler receiverHandler = new MockBlockReceiverHandler();
			BlockTransferProtocol blockXChangeSource = new BlockTransferProtocol(blockToSend, loopBack1.getSource(), loopBack2.getSink());
			BlockTransferProtocol blockXChangeDestination = new BlockTransferProtocol(receiverHandler, remotePeerId, loopBack2.getSource(), loopBack1.getSink());
			
			ProtocolRunner runner1 = new ProtocolRunner(blockXChangeSource, true);
			ProtocolRunner runner2 = new ProtocolRunner(blockXChangeDestination, false);
			
			runner1.start();
			runner2.start();
			runner1.join();
			runner2.join();
	
			assertEquals(1, receiverHandler.nbCall);
			assertEquals(blockToSend.getBlockId(), receiverHandler.receivedBlock.get(0).getBlockId()); 
			assertTrue(Arrays.equals(blockToSend.getBlockData(), ((DataBlock) receiverHandler.receivedBlock.get(0)).getBlockData()));

		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
	}
}

/**
 * <p>Title: BlockRequestProtocolTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.util.Arrays;

import mockObject.MockBlockRepository;

import bcoop.block.DataBlock;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class BlockRequestProtocolTest extends TestCase {
    DataBlock blockToSend = new DataBlock(1234, "data".getBytes()); 

    public final void testExecute() {
        try{
            MockBlockRepository mockBlockRepository = new MockBlockRepository();
            mockBlockRepository.storeBlock(blockToSend);
            
            PipeStream loopBack1 = new PipeStream();
            PipeStream loopBack2 = new PipeStream();
    
            BlockRequestProtocol requestProtClient = new BlockRequestProtocol(blockToSend.getBlockId(), loopBack1.getSource(), loopBack2.getSink());
            BlockRequestProtocol requestProtServer = new BlockRequestProtocol(mockBlockRepository, loopBack2.getSource(), loopBack1.getSink());
            
            ProtocolRunner clientRunner = new ProtocolRunner(requestProtClient, true); 
            ProtocolRunner serverRunner = new ProtocolRunner(requestProtServer, false); 
            
            serverRunner.start();
            clientRunner.start();
            
            clientRunner.join();
            serverRunner.join();
            
            DataBlock receivedBlock = (DataBlock) requestProtClient.getReceivedBlock();
            assertNotNull(receivedBlock);
            assertEquals(blockToSend.getBlockId(), receivedBlock.getBlockId());
            assertTrue(Arrays.equals(blockToSend.getBlockData(), receivedBlock.getBlockData()));
        }
        catch(IOException e){
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }

}

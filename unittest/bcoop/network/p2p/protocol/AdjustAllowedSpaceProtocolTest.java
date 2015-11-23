package bcoop.network.p2p.protocol;

import java.io.IOException;

import mockObject.MockBlockRepository;
import mockObject.MockIdentityManager;
import mockObject.MockRepositoryManager;
import junit.framework.TestCase;

public class AdjustAllowedSpaceProtocolTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testExecute() throws IOException, InterruptedException {
		MockRepositoryManager mockReposManager = new MockRepositoryManager();
        MockBlockRepository mockBlockRepository = new MockBlockRepository();

        PipeStream loopBack1 = new PipeStream();
        PipeStream loopBack2 = new PipeStream();

        AdjustAllowedSpaceProtocol adjustProtClient = new AdjustAllowedSpaceProtocol(10234, loopBack1.getSource(), loopBack2.getSink());
        AdjustAllowedSpaceProtocol adjustProtServer = new AdjustAllowedSpaceProtocol(mockBlockRepository, loopBack2.getSource(), loopBack1.getSink());

        ProtocolRunner clientRunner = new ProtocolRunner(adjustProtClient, true); 
        ProtocolRunner serverRunner = new ProtocolRunner(adjustProtServer, false); 
        
        serverRunner.start();
        clientRunner.start();
        
        clientRunner.join();
        serverRunner.join();
        
        assertEquals(10234, mockBlockRepository.getAllowedSpace());
	}
}

/**
 * <p>Title: ChallengeProtocolTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import mockObject.MockBlockRepository;

import bcoop.block.DataBlock;
import bcoop.network.Challenge;
import bcoop.util.MessageDigest;
import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class ChallengeProtocolTest extends TestCase {
    DataBlock block = new DataBlock(1235456L, null);
    int salt;
    
    public void setUp(){
        Random random = new Random();
        salt = random.nextInt();

        block.setBlockData("alskdmalskdmalsd kmaodijwqodiq wdmoqiwdm qowdm qowkdm qlwkdm lqkwdm ".getBytes());
        
    }

    public final void testExecute() {
        try{
            LoopSocketFactory loopSocket = new LoopSocketFactory();
            Socket socket1 = loopSocket.getSocket();
            Socket socket2 = loopSocket.getSocket();
    
            Challenge challenge = new Challenge(block.getBlockId(), salt);
            challenge.expectedHash = MessageDigest.computeHash(block, salt);
            
            MockBlockRepository mockBlockRepository = new MockBlockRepository();
            mockBlockRepository.storeBlock(block);
            
            ChallengeProtocol challengeProtClient = new ChallengeProtocol(challenge, socket1.getInputStream(), socket1.getOutputStream());
            ChallengeProtocol challengeProtServer = new ChallengeProtocol(mockBlockRepository, socket2.getInputStream(), socket2.getOutputStream());
            
            ProtocolRunner clientRunner = new ProtocolRunner(challengeProtClient, true); 
            ProtocolRunner serverRunner = new ProtocolRunner(challengeProtServer, false); 
            
            serverRunner.start();
            clientRunner.start();
            
            clientRunner.join();
            serverRunner.join();
            
            assertNotNull(challenge.hash);
            assertEquals(challenge.expectedHash.length, challenge.hash.length);
            assertTrue(Arrays.equals(challenge.expectedHash, challenge.hash));
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

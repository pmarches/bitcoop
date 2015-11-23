/**
 * <p>Title: ChallengeProtocol.java</p>
 * <p>Description: A protocol that chalenges another peer to verify it stored a certain block.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bcoop.block.DataBlock;
import bcoop.exception.StreamClosedException;
import bcoop.network.Challenge;
import bcoop.repos.BlockRepository;
import bcoop.util.MessageDigest;

/**
 * @author pmarches
 *
 */
public class ChallengeProtocol extends Protocol {

    private Challenge challenge;
    private BlockRepository blockRepository;

    public ChallengeProtocol(Challenge challenge, InputStream iStream, OutputStream oStream) throws IOException {
        super(Protocol.VERIFY_BLOCK, iStream, oStream);
        this.challenge = challenge; 
    }

    public ChallengeProtocol(BlockRepository blockRepository, InputStream iStream, OutputStream oStream) throws IOException {
        super(Protocol.VERIFY_BLOCK, iStream, oStream);
        this.blockRepository = blockRepository;
    }

    public CommandResult executeClient() throws IOException, StreamClosedException {
        initProtocol();
        
        ooStream.writeLong(challenge.blockId);
        ooStream.writeInt(challenge.salt);
        ooStream.flush();
        
        int len = oiStream.readInt();
        challenge.hash = new byte[len];
        oiStream.readFully(challenge.hash);
		return CommandResult.OK_RESULT;
    }

    public void executeServer() throws IOException, StreamClosedException {
        initProtocol();
        
        long blockId = oiStream.readLong();
        int salt = oiStream.readInt();
        
        byte[] hash = null;
        DataBlock block = (DataBlock) blockRepository.getBlock(blockId);
        if(block != null){
            hash = MessageDigest.computeHash(block, salt);
        }
        
        ooStream.writeInt(hash.length);
        ooStream.write(hash);
        ooStream.flush();
    }

}

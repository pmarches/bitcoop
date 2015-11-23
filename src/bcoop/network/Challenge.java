/**
 * <p>Title: Challenge.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

/**
 * @author pmarches
 *
 */
public class Challenge {
    public int salt;
    public long blockId;
    public byte[] hash;
    public byte[] expectedHash;
    
    public Challenge(long blockId, int salt){
        this.blockId = blockId;
        this.salt = salt;
    }
}

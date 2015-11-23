/**
 * <p>Title: MessageDigest.java</p>
 * <p>Description: Hashing facility</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.util;

import java.security.NoSuchAlgorithmException;

import bcoop.block.DataBlock;

/**
 * @author pmarches
 *
 */
public class MessageDigest {
    /**
     * @param block
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public static byte[] computeHash(DataBlock block, int salt){
        try{
            if(block.getBlockData() == null) return null;
            
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(Utils.intToByteArray(salt));
            md.update(block.getBlockData());
            return md.digest();
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
}

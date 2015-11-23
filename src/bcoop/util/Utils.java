/**
 * <p>Title: Utils.java</p>
 * <p>Description: Utilities I could not put elsewhere</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.util;

/**
 * @author pmarches
 *
 */
public class Utils {

    public static byte[] intToByteArray(int integer){
        byte[] result = new byte[4];
        result[0] = (byte) ((integer >> 0) & 0xFF);
        result[1] = (byte) ((integer >> 8) & 0xFF);
        result[2] = (byte) ((integer >> 16) & 0xFF);
        result[3] = (byte) ((integer >> 24) & 0xFF);
        return result;
    }

    /**
     * @param length
     * @return
     */
    public static byte[] shortToByteArray(short aShort) {
        byte[] result = new byte[2];
        result[0] = (byte) ((aShort >> 0) & 0xFF);
        result[1] = (byte) ((aShort >> 8) & 0xFF);
        return result;
    }

    /**
     * @param blockData
     * @return
     */
    public static short byteArrayToShort(byte[] byteArray) {
        return (short) ((byteArray[1] << 8 ) | (byteArray[0] & 0xFF));        
    }

}

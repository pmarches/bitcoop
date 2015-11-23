/**
 * <p>Title: UtilsTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.util;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class UtilsTest extends TestCase {

    public final void testIntToByteArray() {
        byte[] zeroes = Utils.intToByteArray(0);
        assertEquals(4, zeroes.length);
        for(int i=0; i<zeroes.length; i++){
            assertEquals(0, zeroes[i]);
        }

        byte[] number = Utils.intToByteArray(0xFF34A21B);
        assertEquals(4, number.length);
        assertEquals((byte) 0xFF, number[3]);
        assertEquals((byte) 0x34, number[2]);
        assertEquals((byte) 0xA2, number[1]);
        assertEquals((byte) 0x1B, number[0]);

    }

    public final void testShortToByteArray() {
        short shortValue = (short) 0xF2A6;
        byte[] aShortArray = Utils.shortToByteArray(shortValue);
        assertEquals(2, aShortArray.length);
        assertEquals(shortValue, Utils.byteArrayToShort(aShortArray));
    }

}

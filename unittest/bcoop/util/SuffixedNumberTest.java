package bcoop.util;

import junit.framework.TestCase;

public class SuffixedNumberTest extends TestCase {

	/*
	 * Test method for 'bcoop.util.SuffixedNumber.toString()'
	 */
	public void testToString() {
		assertEquals("100 bytes", new SuffixedNumber(100, "bytes").toString());
		assertEquals("1.04 Kilobytes", new SuffixedNumber(1040, "bytes").toString());
		assertEquals("1.20 Gigabytes", new SuffixedNumber(1205000000, "bytes").toString());
		assertEquals("0 bytes", new SuffixedNumber(0, "bytes").toString());
	}
	
	public void testFromString(){
		assertEquals(100, SuffixedNumber.fromString("100 bytes"));
		assertEquals(1040, SuffixedNumber.fromString("1.04 kilobytes"));
		assertEquals(2640000, SuffixedNumber.fromString("2.64 Mega"));
		assertEquals(2640, SuffixedNumber.fromString("2.64 K"));
		assertEquals(2640, SuffixedNumber.fromString("2.64K"));
	}
}

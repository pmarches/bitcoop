package bcoop.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import junit.framework.TestCase;

public class HashOfArrayTest extends TestCase {
	final static long VAL1 = 0x41293827123L;
	final static long VAL2 = 0x41E3A2353L;
	final static long VAL3 = 0x03L;
	
	/*
	 * Test method for 'bcoop.util.HashOfArray.getArray(K)'
	 */
	public void testHashOfArray() {
		HashOfArray<String, Long> hoa = new HashOfArray<String, Long>();
		hoa.addValue("key1", VAL1);
		hoa.addValue("key2", VAL2);
		hoa.addValue("key1", VAL3);
		
		Vector<Long> key1Array = hoa.getAllBlockOfPeer("key1");
		assertEquals(2, key1Array.size());
		assertEquals(VAL1, key1Array.get(0).longValue());
		assertEquals(VAL3, key1Array.get(1).longValue());

		Vector<Long> key2Array = hoa.getAllBlockOfPeer("key2");
		assertEquals(1, key2Array.size());
		assertEquals(VAL2, key2Array.get(0).longValue());
		
		assertNull(hoa.getAllBlockOfPeer("badKey"));
	}
	
	public void testSerialize(){
		try{
			HashOfArray<String, Long> src = new HashOfArray<String, Long>();
			src.addValue("key1", VAL1);
			src.addValue("key2", VAL2);
			src.addValue("key1", VAL3);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(src);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
			HashOfArray newHash = (HashOfArray) ois.readObject();
			
			assertEquals(src, newHash);
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

}

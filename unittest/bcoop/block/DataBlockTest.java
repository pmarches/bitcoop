package bcoop.block;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

public class DataBlockTest extends TestCase {
	public void testSerialization() throws IOException, ClassNotFoundException{
		ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
		ObjectOutputStream bOut = new ObjectOutputStream(arrayStream);
		DataBlock dBlock = new DataBlock(0x123445, "allo".getBytes());
		bOut.writeObject(dBlock);
		
		ObjectInputStream bIn = new ObjectInputStream(new ByteArrayInputStream(arrayStream.toByteArray()));
		DataBlock dBlockRead = (DataBlock) bIn.readObject();
		assertEquals(0, bIn.available());
		assertEquals(dBlock, dBlockRead);
	}
}

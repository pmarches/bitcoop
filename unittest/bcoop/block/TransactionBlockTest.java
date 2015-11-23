package bcoop.block;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

public class TransactionBlockTest extends TestCase {
	public final void testSerialize() throws IOException, ClassNotFoundException{
		TransactionBlock tBlockSrc = new TransactionBlock(0x1, "dataName",  "scheduleName");
		tBlockSrc.transactionSize=123;
		tBlockSrc.endTransaction();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeUnshared(tBlockSrc);
		tBlockSrc.transactionSize=567;
		oos.writeUnshared(tBlockSrc);
		
		byte[] data = baos.toByteArray();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		TransactionBlock destBlock1 = (TransactionBlock) ois.readObject();

		assertEquals(tBlockSrc.blockId, destBlock1.blockId);
		assertEquals(123, destBlock1.transactionSize);
		assertEquals(tBlockSrc.getTransactionEndTime(), destBlock1.getTransactionEndTime());
		assertEquals(tBlockSrc.getTransactionStartTime(), destBlock1.getTransactionStartTime());

		TransactionBlock destBlock2 = (TransactionBlock) ois.readObject();
		assertNotSame(destBlock1, destBlock2);
		assertEquals(tBlockSrc.blockId, destBlock2.blockId);
		assertEquals(tBlockSrc.transactionSize, destBlock2.transactionSize);
		assertEquals(tBlockSrc.getTransactionEndTime(), destBlock2.getTransactionEndTime());
		assertEquals(tBlockSrc.getTransactionStartTime(), destBlock2.getTransactionStartTime());
	}
}

package bcoop.backup.blockfactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Pipe.SinkChannel;

import bcoop.block.DataBlock;

import junit.framework.TestCase;

public class ZipBlockProcessorTest extends TestCase {

	public void testProcessFile() throws IOException{
		File file = new File("testData/filesToBackup/oldStaticImportantStuff/archives1.dat");
		long fileSize = file.length();
		FileInputStream fis = new FileInputStream(file);
		ReadableByteChannel sourceChannel = fis.getChannel();

		long totalCompressed = 0;
		ZipBlockProcessor zProc = new ZipBlockProcessor();
		while(true){
			byte[] zippedData = zProc.compressBlock(sourceChannel, 100000);
			DataBlock blockToBeProcessed1 = new DataBlock(0x1, zippedData);
			totalCompressed += blockToBeProcessed1.getStorageSizeOfBlock();
			if(zProc.getSourceChannelOffset() >= fileSize){
				break;
			}
		}
//		System.out.println(totalCompressed / (double) totalUncompressed);
	}
	
	public void testProcessBlock() throws IOException {
		final int BLOCK_SIZE = 130;
		Pipe pipe = Pipe.open();
		ByteBuffer dataToSend = ByteBuffer.allocate(600);
		fill(dataToSend);
		pipe.sink().write(dataToSend);
		pipe.sink().close();
		dataToSend.rewind(); //Reset the position to 0
		
		ZipBlockProcessor zProc = new ZipBlockProcessor();
		byte[] zippedData = zProc.compressBlock(pipe.source(), BLOCK_SIZE);
		int uncompressedSizeOfBlock1 = (int) zProc.getSourceChannelOffset();
		DataBlock blockToBeProcessed1 = new DataBlock(0x1, zippedData);
		assertTrue(zippedData.length > 30);
//		assertTrue(zippedData.length <= 50);
		
//		System.out.println("Ratio="+(blockToBeProcessed1.getStorageSizeOfBlock()/(double)nbUncompressedBytesRead1));
		zippedData = zProc.compressBlock(pipe.source(), BLOCK_SIZE);
		int uncompressedSizeOfBlock2 = (int) zProc.getSourceChannelOffset()-uncompressedSizeOfBlock1;
		DataBlock blockToBeProcessed2 = new DataBlock(0x2, zippedData);
		assertEquals(600, zProc.getSourceChannelOffset());
		
		pipe = Pipe.open();
		SinkChannel sink = pipe.sink();
		UnzipBlockProcessor uzProc = new UnzipBlockProcessor();
		byte[] unzipppedData1 = new byte[uncompressedSizeOfBlock1];
		uzProc.uncompressBlock(blockToBeProcessed1.getBlockData(), unzipppedData1);
		sink.write(ByteBuffer.wrap(unzipppedData1));

		//Normally, we would need to seek to the proper position in the file..
		byte[] unzipppedData2 = new byte[uncompressedSizeOfBlock2];
		uzProc.uncompressBlock(blockToBeProcessed2.getBlockData(), unzipppedData2);
		sink.write(ByteBuffer.wrap(unzipppedData2));

		pipe.sink().close();
		ByteBuffer unzipped = ByteBuffer.allocate((int) zProc.getSourceChannelOffset());
		pipe.source().read(unzipped);
		unzipped.flip();

		assertEquals(dataToSend, unzipped);
		assertEquals(dataToSend.capacity(), unzipped.capacity());
	}

	private void fill(ByteBuffer dataToSend) throws IOException {
		FileInputStream fis = new FileInputStream("testData/filesToBackup/oldStaticImportantStuff/archives1.dat");
		while(dataToSend.hasRemaining()){
			dataToSend.put((byte) fis.read());
		}
		dataToSend.flip();
	}

}

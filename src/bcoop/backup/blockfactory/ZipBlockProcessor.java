package bcoop.backup.blockfactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.Deflater;

import bcoop.block.DataBlock;
import bcoop.util.ByteArrayChannel;

public class ZipBlockProcessor {
	public static final int SIZE_OF_ZIP_HEADER = 10;
	Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
	ByteBuffer srcBuffer;
	ByteBuffer outputBuffer;
	long positionInSourceChannel;

	public ZipBlockProcessor(){
		this.srcBuffer = ByteBuffer.allocate(1024);
	}
	
	public byte[] compressBlock(ReadableByteChannel sourceChannel, int requestedBlockSize) throws IOException{
		if(requestedBlockSize <= SIZE_OF_ZIP_HEADER){
			throw new RuntimeException("Block is too small!");
		}
		if(outputBuffer == null || outputBuffer.capacity() < requestedBlockSize){
			outputBuffer = ByteBuffer.allocate(requestedBlockSize);
		}

		this.deflater.reset();
		int uncompressedSizeOfBlock = 0;

		while(true){
			if(deflater.needsInput()){
				srcBuffer.position(0);
				int safeNumberOfBytesToRead = Math.min(outputBuffer.remaining(), srcBuffer.capacity());
				srcBuffer.limit(safeNumberOfBytesToRead-SIZE_OF_ZIP_HEADER);
				int nbBytesRead = sourceChannel.read(this.srcBuffer);
				if(nbBytesRead == -1){
					deflater.finish();
					break;
				}
				else{
					srcBuffer.flip();
					deflater.setInput(srcBuffer.array(), srcBuffer.position(), srcBuffer.remaining());
					uncompressedSizeOfBlock+=nbBytesRead;
				}
			}

			int compressedSize = deflater.deflate(outputBuffer.array(), outputBuffer.position(), outputBuffer.remaining());
			if(compressedSize == 0){
				deflater.finish(); //Let's re-try the compression
				compressedSize = deflater.deflate(outputBuffer.array(), outputBuffer.position(), outputBuffer.remaining());
			}
			else{
				System.err.println("First decomp was not 0 but "+compressedSize);
			}
			outputBuffer.position(outputBuffer.position()+compressedSize);
			if(deflater.finished()){
				if(outputBuffer.remaining() <= SIZE_OF_ZIP_HEADER){ //The output is full
					break;
				}
				else{
					deflater.reset();
				}
			}
			
		}
		outputBuffer.flip();
		positionInSourceChannel += uncompressedSizeOfBlock;
		byte[] snugArray = new byte[outputBuffer.limit()];
		System.arraycopy(outputBuffer.array(), 0, snugArray, 0, outputBuffer.limit());
		return snugArray;
	}

	public void compressBlock(DataBlock blockToCompress) throws IOException {
		ByteArrayChannel inputChannel = new ByteArrayChannel(blockToCompress.getBlockData());
		
		byte[] zippedBytes = compressBlock(inputChannel, blockToCompress.getBlockData().length);
		blockToCompress.setBlockData(zippedBytes);
	}

	public long getSourceChannelOffset() {
		return this.positionInSourceChannel;
	}
}

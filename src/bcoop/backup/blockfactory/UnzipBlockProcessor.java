package bcoop.backup.blockfactory;

import java.io.IOException;
import java.util.zip.Inflater;

public class UnzipBlockProcessor {
	Inflater inflater = new Inflater(true);

	public UnzipBlockProcessor() {
	}

	public void uncompressBlock(byte[] zippedData, byte[] unzippedData) throws IOException {
		if(unzippedData == null){
			throw new RuntimeException("UnzippedData must be allocated first!");
		}
		try{
			int zippedDataOffset = 0;
			int unzippedDataOffset = 0;
			while(true){
				inflater.reset();
				inflater.setInput(zippedData, zippedDataOffset, zippedData.length-zippedDataOffset);
				int uncompressedSize = inflater.inflate(unzippedData, unzippedDataOffset, unzippedData.length-unzippedDataOffset);
				if(zippedDataOffset == zippedData.length){
					break;
				}
				zippedDataOffset = zippedData.length - inflater.getRemaining();
				unzippedDataOffset+=uncompressedSize;
			}
		}
		catch(Exception e){
			throw new IOException(e.getLocalizedMessage());
		}
		
//		while(inflater.getRemaining()!=0){
//			try {
//				int uncompressedSize = inflater.inflate(uncompressedBuffer.array());
//				if(uncompressedSize== 0){
//					zippedDataOffset += zippedData.length - inflater.getRemaining();
//					inflater.reset();
//					inflater.setInput(zippedData, zippedDataOffset, zippedData.length - zippedDataOffset);
//				}
//				else{
//					uncompressedBuffer.limit(uncompressedBuffer.position()+uncompressedSize);
//					sink.write(uncompressedBuffer);
//					uncompressedBuffer.clear();
//				}
//			} catch (DataFormatException e) {
//				e.printStackTrace();
//				throw new IOException(e.getLocalizedMessage());
//			}
	}

}

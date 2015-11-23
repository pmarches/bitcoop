package bcoop.util;

import java.util.ArrayList;

public class ByteBufferArray {
	ArrayList<byte[]> listOfByteBuffer = new ArrayList<byte[]>();
	int lastValidArray;
	int limit;

	public void copyBytesFrom(byte[] srcBytes, int srcOffset, int nbBytesToCopy, int destOffset){
		int startArray=0;
		for(startArray=0; startArray<lastValidArray; startArray++){
			byte[] currentArray = listOfByteBuffer.get(startArray);
			if(destOffset >= startArray+currentArray.length){
				break;
			}
		}
		
				
	}
	
	public void append(byte[] newBuffer){
		listOfByteBuffer.add(newBuffer);
	}
	
	public byte[] getBytes(){
		int totalBytes=0;
		for(int i=0; i<lastValidArray; i++){
			byte[] currentBuffer = listOfByteBuffer.get(i);
			totalBytes += currentBuffer.length;
		}
		int currentOffset = 0;
		byte[] consolidatedBytes = new byte[totalBytes];
		for(byte[] currentBuffer : listOfByteBuffer){
			System.arraycopy(currentBuffer, 0, consolidatedBytes, currentOffset, currentBuffer.length);
			currentOffset+=currentBuffer.length;
		}
		if(currentOffset != totalBytes){
			throw new RuntimeException("This should not happen "+currentOffset+ " "+totalBytes);
		}
		return consolidatedBytes;
	}
}

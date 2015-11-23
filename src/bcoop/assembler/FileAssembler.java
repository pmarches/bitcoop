package bcoop.assembler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.crypto.SecretKey;

import bcoop.backup.blockfactory.UnzipBlockProcessor;
import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.block.MetaDataBlock;
import bcoop.crypto.Decryptor;
import bcoop.util.BitCoopFile;

public class FileAssembler extends FSAssembler {
	private FileChannel rafChannel;
	private UnzipBlockProcessor uzProc;
	private Decryptor blockDecryptor;

	public FileAssembler(String assemblyDir, HeaderBlock hBlock, SecretKey decryptionKey) throws FileNotFoundException{
		super(assemblyDir, hBlock);
		BitCoopFile assemblyTargetDir = this.assemblyPath.getParentFile();
		assemblyTargetDir.mkdirs();
		RandomAccessFile raf = new RandomAccessFile(this.assemblyPath, "rw");
		rafChannel = raf.getChannel();
		this.uzProc = new UnzipBlockProcessor();
		this.blockDecryptor = new Decryptor(decryptionKey, hBlock.getEncryptionIV());
	}

	@Override
	public void assembleBlock(DataBlock block) throws IOException {
		hBlock.checkValidBlock(block);
		MetaDataBlock mBlock = hBlock.getMetaDataForDataBlock(block.getBlockId());
		rafChannel.position(mBlock.getOffset());
		
		try{
			byte[] clearData = this.blockDecryptor.decrypt(block.getBlockData());
			byte[] uncompressed = new byte[mBlock.getSize()]; //TODO should not write the whole block at once, use smaller chunks..
			uzProc.uncompressBlock(clearData, uncompressed);
			rafChannel.write(ByteBuffer.wrap(uncompressed));
		}
		catch(Exception e){
			e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}

	}

	@Override
	public void closeAssembler() throws IOException {
		rafChannel.close();
	}
}

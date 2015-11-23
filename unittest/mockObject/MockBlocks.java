package mockObject;

import java.util.Random;

import junit.framework.TestCase;

import bcoop.backup.blockfactory.ZipBlockProcessor;
import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.block.TransactionBlock;
import bcoop.crypto.Encryptor;

public abstract class MockBlocks extends TestCase {
	public static final String LINK_DESTINATION = "/tmp";
	public static DataBlock[] CLEAR_DATABLOCK;
	public static DataBlock[] ZIPPED_DATABLOCK;
	public static DataBlock[] ENCRYPTED_DATABLOCK;
	
	public static HeaderBlock HEADER_BLOCKS[];
	public static TransactionBlock TRANSACTION_BLOCKS[];
	private static byte[][] ENCRYPTION_IV;

	static{
		try {
			CLEAR_DATABLOCK = new DataBlock[3];
			CLEAR_DATABLOCK[0] = new DataBlock(0x1, generateClearData(75000));
			CLEAR_DATABLOCK[1] = new DataBlock(0x2, generateClearData(25000));
			CLEAR_DATABLOCK[2] = new DataBlock(0x3, generateClearData(102400));
	
			ZipBlockProcessor zipper = new ZipBlockProcessor();
			ZIPPED_DATABLOCK = new DataBlock[CLEAR_DATABLOCK.length];
			for(int i=0; i<ZIPPED_DATABLOCK.length; i++){
				ZIPPED_DATABLOCK[i] = new DataBlock(i, CLEAR_DATABLOCK[i].getBlockData());
					zipper.compressBlock(ZIPPED_DATABLOCK[i]);
			}

			Encryptor encryptor = new Encryptor(MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey());
			ENCRYPTED_DATABLOCK = new DataBlock[CLEAR_DATABLOCK.length];
			ENCRYPTION_IV = new byte[ENCRYPTED_DATABLOCK.length][];

			byte[] cipherText = encryptor.encrypt(ZIPPED_DATABLOCK[0].getBlockData());
			ENCRYPTED_DATABLOCK[0] = new DataBlock(0x1, cipherText);
			
			cipherText = encryptor.encrypt(ZIPPED_DATABLOCK[1].getBlockData());
			ENCRYPTED_DATABLOCK[1] = new DataBlock(0x2, cipherText);
			ENCRYPTION_IV[0] = encryptor.getIV();
			assertNotNull(ENCRYPTION_IV[0]);

			encryptor = new Encryptor(MockIdentityManager.CLIENT_LOCAL_ID.getEncryptionKey());
			cipherText = encryptor.encrypt(ZIPPED_DATABLOCK[2].getBlockData());
			ENCRYPTED_DATABLOCK[2] = new DataBlock(0x3, cipherText);
			ENCRYPTION_IV[1] = encryptor.getIV();
			assertNotNull(ENCRYPTION_IV[1]);

			HEADER_BLOCKS = new HeaderBlock[4];
			HEADER_BLOCKS[0] = new HeaderBlock(0x100, "OneHunderedKFileTwoBlocks.txt", System.currentTimeMillis()-3600);
			HEADER_BLOCKS[0].setFSObjectType(HeaderBlock.FS_FILE_OBJECT);
			HEADER_BLOCKS[0].registerDataBlock(ENCRYPTED_DATABLOCK[0], 0, CLEAR_DATABLOCK[0].getStorageSizeOfBlock());
			HEADER_BLOCKS[0].registerDataBlock(ENCRYPTED_DATABLOCK[1], CLEAR_DATABLOCK[0].getStorageSizeOfBlock(), CLEAR_DATABLOCK[1].getStorageSizeOfBlock());
			HEADER_BLOCKS[0].setEncryptionIV(ENCRYPTION_IV[0]);

			HEADER_BLOCKS[1] = new HeaderBlock(0x101, "OneMegFile.txt", System.currentTimeMillis());
			HEADER_BLOCKS[1].setFSObjectType(HeaderBlock.FS_FILE_OBJECT);
			HEADER_BLOCKS[1].registerDataBlock(ENCRYPTED_DATABLOCK[2], 0, CLEAR_DATABLOCK[2].getStorageSizeOfBlock());
			HEADER_BLOCKS[1].setEncryptionIV(ENCRYPTION_IV[1]);

			HEADER_BLOCKS[2] = new HeaderBlock(0x102, "ADirectory", System.currentTimeMillis());
			HEADER_BLOCKS[2].setFSObjectType(HeaderBlock.FS_DIRECTORY_OBJECT);

			HEADER_BLOCKS[3] = new HeaderBlock(0x103, "ALink.txt", 200);
			HEADER_BLOCKS[3].setFSObjectType(HeaderBlock.FS_LINK_OBJECT);
			HEADER_BLOCKS[3].setProperty(HeaderBlock.PROPNAME_LINK_DEST, LINK_DESTINATION);

			TRANSACTION_BLOCKS = new TransactionBlock[2];
			TRANSACTION_BLOCKS[0] = new TransactionBlock(0x1000, "mockDataName", "mockDataSchedule");
			TRANSACTION_BLOCKS[0].addHeaderBlockToTransaction(HEADER_BLOCKS[0]);
			TRANSACTION_BLOCKS[0].addHeaderBlockToTransaction(HEADER_BLOCKS[1]);

			TRANSACTION_BLOCKS[1] = new TransactionBlock(0x1001, "mockDataName", "mockDataSchedule");
			TRANSACTION_BLOCKS[1].addHeaderBlockToTransaction(HEADER_BLOCKS[0]);
			TRANSACTION_BLOCKS[1].addHeaderBlockToTransaction(HEADER_BLOCKS[1]);
			TRANSACTION_BLOCKS[1].addHeaderBlockToTransaction(HEADER_BLOCKS[2]);
			TRANSACTION_BLOCKS[1].addHeaderBlockToTransaction(HEADER_BLOCKS[3]);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static byte[] generateClearData(int dataSize) {
		Random rnd = new Random();
		byte data[] = new byte[dataSize];
		rnd.nextBytes(data);
		return data;
	}
}

/**
 * <p>Title: BlockAssembly.java</p>
 * <p>Description: Allows the assembly of many blocks into a file. The blocks may arruve in any order.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.assembler;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;

/**
 * @author pmarches
 *
 */
public class BlockAssembler {
	FSAssembler fsAssembler = null;
	String assemblyDir;
	
	public BlockAssembler(String assemblyDir, HeaderBlock hBlock, SecretKey decryptionKey) throws FileNotFoundException{
		this.assemblyDir = assemblyDir;
		switch(hBlock.getFSObjectType()){
		case HeaderBlock.FS_FILE_OBJECT:
			fsAssembler = new FileAssembler(assemblyDir, hBlock, decryptionKey);
			break;
		case HeaderBlock.FS_DIRECTORY_OBJECT:
			fsAssembler = new DirectoryAssembler(assemblyDir, hBlock);
			break;
		case HeaderBlock.FS_LINK_OBJECT:
			fsAssembler = new LinkAssembler(assemblyDir, hBlock);
			break;
		case HeaderBlock.FS_BLOCKDEV_OBJECT:
			fsAssembler = new BlockDevAssembler(assemblyDir, hBlock);
			break;
		default:
			Logger.getLogger(this.getClass()).error("Unkown type of FS object"+hBlock.getFSObjectType());
		}
	}

	public void assembleBlock(DataBlock block) throws IOException {
		fsAssembler.assembleBlock(block);
	}

	public void closeAssembly() throws IOException {
		fsAssembler.closeAssembler();
	}

	public String getAssembledPath() {
		return fsAssembler.assemblyPath.getAbsolutePath();
	}
	
}


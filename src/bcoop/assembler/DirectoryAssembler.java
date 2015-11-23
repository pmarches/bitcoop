package bcoop.assembler;

import java.io.IOException;

import org.apache.log4j.Logger;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;

public class DirectoryAssembler extends FSAssembler {
	public DirectoryAssembler(String assemblyDir, HeaderBlock hBlock) {
		super(assemblyDir, hBlock);
		this.assemblyPath.mkdirs();
		
		//TODO set file mode..
	}

	@Override
	public void assembleBlock(DataBlock dBlock) throws IOException {
		Logger.getLogger(this.getClass()).error("Should not call assembleBlock for a directory, discarding block");
	}

	@Override
	public void closeAssembler() throws IOException {
	}

}

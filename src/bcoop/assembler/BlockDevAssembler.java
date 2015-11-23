package bcoop.assembler;

import java.io.IOException;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;

public class BlockDevAssembler extends FSAssembler {

	public BlockDevAssembler(String assemblyDir, HeaderBlock hBlock) {
		super(assemblyDir, hBlock);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void assembleBlock(DataBlock dBlock) throws IOException {
	}

	@Override
	public void closeAssembler() throws IOException {
	}

}

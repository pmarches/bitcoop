package bcoop.assembler;

import java.io.IOException;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.util.BitCoopFile;

public abstract class FSAssembler {
	protected HeaderBlock hBlock;
	protected BitCoopFile assemblyPath;
	
	public FSAssembler(String assemblyDir, HeaderBlock hBlock){
		this.hBlock = hBlock;
		//We have to remove the : from the path on windows
		String safePath = hBlock.getFilename().replaceAll(":", "");
		this.assemblyPath = new BitCoopFile(assemblyDir+"/"+safePath);
	}

	abstract public void assembleBlock(DataBlock dBlock) throws IOException;
	abstract public void closeAssembler() throws IOException;

	protected void assembleProperties(BitCoopFile bcFile) {
		String modeStr = this.hBlock.getProperty(HeaderBlock.PROPNAME_MODE);
		if(modeStr != null){
			bcFile.setMode(Integer.parseInt(modeStr));
		}
		//TODO add more properties here..
	}

}

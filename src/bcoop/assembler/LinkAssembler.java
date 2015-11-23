package bcoop.assembler;

import java.io.IOException;

import org.apache.log4j.Logger;

import bcoop.block.DataBlock;
import bcoop.block.HeaderBlock;
import bcoop.util.BitCoopFile;
import bcoop.util.OSCapabilities;

public class LinkAssembler extends FSAssembler {

	public LinkAssembler(String assemblyDir, HeaderBlock hBlock) {
		super(assemblyDir, hBlock);
		if(OSCapabilities.isLinkSupported()==false){
			Logger.getLogger(this.getClass()).warn("Will not create link named "+hBlock.getFilename()+" beacuse this OS does not support links.");
			return;
		}
		String linkDestination = hBlock.getProperty(HeaderBlock.PROPNAME_LINK_DEST);
		BitCoopFile bcFile = new BitCoopFile(assemblyPath.getAbsolutePath());
		assembleProperties(bcFile);
		bcFile.createSymbolicLink(linkDestination);
	}

	@Override
	public void assembleBlock(DataBlock dBlock) throws IOException {
		Logger.getLogger(this.getClass()).error("Link "+hBlock.getFilename()+" do not require data blocks, discarding block.");
	}

	@Override
	public void closeAssembler() throws IOException {
	}

}

package bcoop.network.p2p.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import bcoop.exception.StorageLimitException;
import bcoop.exception.StreamClosedException;
import bcoop.repos.BlockRepository;

public class AdjustAllowedSpaceProtocol extends Protocol {

	private long newAllowedSpace;
	private BlockRepository bRepository;

	public AdjustAllowedSpaceProtocol(long newAllowedSpace, InputStream iStream, OutputStream oStream) throws IOException {
		super(Protocol.ADJUST_SPACE, iStream, oStream);
		this.newAllowedSpace = newAllowedSpace;
	}

	public AdjustAllowedSpaceProtocol(BlockRepository bRepository, InputStream iStream, OutputStream oStream) throws IOException {
		super(Protocol.ADJUST_SPACE, iStream, oStream);
		this.bRepository = bRepository;
	}

	@Override
	public CommandResult executeClient() throws IOException, StreamClosedException {
		initProtocol();

		this.ooStream.writeLong(this.newAllowedSpace);
		this.ooStream.flush();
		
		return getResult();
	}

	@Override
	public void executeServer() throws IOException, StreamClosedException {
        CommandResult result = CommandResult.OK_RESULT;;
		initProtocol();
		
		long newSpace = this.oiStream.readLong();
		if(newSpace < this.bRepository.getAllowedSpace()){
			Logger.getLogger(this.getClass()).warn("Remote peer decreased our allowed space from "+this.bRepository.getAllowedSpace()+ " to "+newSpace);
		}

		try{
			this.bRepository.setAllowedSpace(newSpace);
		}
		catch(StorageLimitException sle){
			result = new CommandResult(sle);
		}

		this.ooStream.writeObject(result);
		this.ooStream.flush();
	}

}

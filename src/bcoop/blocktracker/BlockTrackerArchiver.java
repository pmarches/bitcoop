package bcoop.blocktracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import bcoop.exception.MissingConfigurationException;
import bcoop.network.Network;
import bcoop.util.Configuration;

public class BlockTrackerArchiver {
	public static String getTrackerFilename(Configuration config) throws MissingConfigurationException{
		String baseDir = config.getProperty(Configuration.BASE_DIR);
		if(baseDir == null){
			throw new MissingConfigurationException();
		}
		return  baseDir+"/tracker";
	}

	public static void saveBlockTracker(BlockTracker blockTracker, OutputStream oStream){
		try{
			Logger.getLogger(BlockTrackerArchiver.class).debug("Saving BlockTracker to Archive");
			ObjectOutputStream oos = new ObjectOutputStream(oStream);
			oos.writeObject(blockTracker);
			oos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static BlockTracker loadTracker(int transactionHistorySize, Network network, InputStream iStream) throws IOException {
		if(network == null){
			throw new RuntimeException("Network cannot be null for the BlockTracker.");
		}
		BlockTracker blockTracker = null; 
		if(iStream == null){
			Logger.getLogger(BlockTrackerArchiver.class).info("Creating a new Empty BlockTracker");
			blockTracker = new BlockTracker(transactionHistorySize, network); 
		}
		else{
			try {
				ObjectInputStream ois = new ObjectInputStream(iStream);
				blockTracker = (BlockTracker) ois.readObject();
				blockTracker.network = network;
		        Logger.getLogger(BlockTrackerArchiver.class).debug("Loaded BlockTracker from archive");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		blockTracker.setTransactionHistorysize(transactionHistorySize);
		return blockTracker;
	}

}

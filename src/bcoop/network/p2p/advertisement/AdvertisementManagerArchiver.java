package bcoop.network.p2p.advertisement;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import bcoop.exception.MissingConfigurationException;
import bcoop.util.Configuration;

public class AdvertisementManagerArchiver {
	/**
	 * @param stream
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static AdvertisementManager loadFrom(InputStream iStream) throws IOException{
		Logger.getLogger(AdvertisementManagerArchiver.class).debug("Loading AdvertisementManager");
		ObjectInputStream ois = new ObjectInputStream(iStream);
		try{
            AdvertisementManager adMan = (AdvertisementManager) ois.readObject();
            adMan.removeExpiredAdvertisements();
            return adMan;
		}
		catch(ClassNotFoundException e){
			//Can't happend
			return null;
		}
	}

	public static void saveTo(AdvertisementManager aManager, OutputStream oStream){
		Logger.getLogger(AdvertisementManagerArchiver.class).debug("Saving AdvertisementManager with "+aManager.getNumberOfRemotePeers()+ " peers.");
		try{
			ObjectOutputStream oos = new ObjectOutputStream(oStream); 
			oos.writeObject(aManager);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static String getAdvertisementFilePath(Configuration config) throws MissingConfigurationException {
		String advertisementFile = config.getProperty(Configuration.AD_MAN_FILE);
		String baseDir = config.getProperty(Configuration.BASE_DIR);
		String fullPath = baseDir+"/"+advertisementFile;
		
		if(advertisementFile == null || baseDir == null){
			Logger.getLogger(AdvertisementManagerArchiver.class).warn("Could not use advertisement file path: "+fullPath);
			throw new MissingConfigurationException();
		}
		
		return fullPath;
	}

}

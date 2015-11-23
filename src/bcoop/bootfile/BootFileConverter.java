package bcoop.bootfile;

import java.io.IOException;

import bcoop.identity.LocalIdentity;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.network.p2p.advertisement.PeerAdvertisement;
import bcoop.util.BitCoopFile;

public class BootFileConverter {
	public static void exportBootFile(BitCoopFile exportFile, LocalIdentity localId, AdvertisementManager adMan) throws IOException{
		BootFile bootFile = createBootfile(localId, adMan);
		bootFile.saveToFile(exportFile);
	}
	
	public static void importBootFile(BitCoopFile importfile){
		//This should go in the BCoopServer class..
	}
	
	public static BootFile createBootfile(LocalIdentity peerName, AdvertisementManager adMan){
		BootFile bootFile = new BootFile();
		bootFile.setLocalIdentity(peerName);
		//TODO add private and public key
		PeerAdvertisement[] knownPeers = adMan.getPeerAdvertisementArray();
		for(PeerAdvertisement currentPeer : knownPeers){
			bootFile.addPeer(currentPeer);			
		}
		return bootFile;
	}
}

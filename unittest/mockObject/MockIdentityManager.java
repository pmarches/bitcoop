package mockObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import bcoop.identity.Identity;
import bcoop.identity.IdentityGenerator;
import bcoop.identity.IdentityManager;
import bcoop.identity.IdentityTest;
import bcoop.identity.LocalIdentity;
import bcoop.util.ObjectStore;

public class MockIdentityManager extends IdentityManager {
	public static final Identity PEER_ID1 = new Identity(0x1, "identity1", (PublicKey) null);
	public static final Identity PEER_ID2 = new Identity(0x2, "identity2", (PublicKey) null);
	public static final Identity PEER_ID3 = new Identity(0x3, "peerNotInManager1", (PublicKey) null);
	public static final Identity UNREACHABLE_PEER_ID = new Identity(0x4, "unreachableIdentity", (PublicKey) null);
	
	public static LocalIdentity CLIENT_LOCAL_ID;
	public static LocalIdentity SERVER_LOCAL_ID;
	public static Identity CLIENT_ID;
	public static Identity SERVER_ID;

	final public static String CLIENT_IDENTITIES_PATH="testData/clientIdentityManager.dat";
	final public static String SERVER_IDENTITIES_PATH="testData/serverIdentityManager.dat";
    public static MockIdentityManager mockIdentityManagerServer;
    public static MockIdentityManager mockIdentityManagerClient;
    
    static{
    	try{
    		mockIdentityManagerClient = new MockIdentityManager(MockIdentityManager.CLIENT_IDENTITIES_PATH);
    		CLIENT_LOCAL_ID = mockIdentityManagerClient.getLocalIdentity();
    		CLIENT_ID = CLIENT_LOCAL_ID.getBaseIdentity();
    		
    		mockIdentityManagerServer = new MockIdentityManager(MockIdentityManager.SERVER_IDENTITIES_PATH);
    		SERVER_LOCAL_ID = mockIdentityManagerServer.getLocalIdentity();
    		SERVER_ID = SERVER_LOCAL_ID.getBaseIdentity();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }

    private Hashtable<Long, Identity> identities;

	public MockIdentityManager(String path) throws IOException{
		File saveFile = new File(path);
		if(saveFile.exists()){
			if(saveFile.canRead()==false){
				throw new IOException("Cannot read identities file "+path);
			}
			if(saveFile.canWrite()==false){
				Logger.getLogger(this.getClass()).warn("Identity file '"+path+"' is read only.");
			}
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile));
				identities = (Hashtable<Long, Identity>) ois.readObject();
				LocalIdentity localIdentity = (LocalIdentity) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		else{
			this.identities = new Hashtable<Long, Identity>();
		}

		
		if(countIdentities() == 0){
			IdentityGenerator idGen = new IdentityGenerator(IdentityTest.getPrecomputedDSAParameters());
			LocalIdentity localIdentity = null;
			if(path.equals(CLIENT_IDENTITIES_PATH)){
				localIdentity = idGen.generateLocalIdentity("clientIdentity");
			}
			else if(path.equals(SERVER_IDENTITIES_PATH)){
				localIdentity = idGen.generateLocalIdentity("serverIdentity");
			}
			
			setLocalIdentity(localIdentity);
			addIdentity(PEER_ID1);
			addIdentity(PEER_ID2);
		}
	}
}

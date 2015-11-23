/**
 * <p>Title: MockNetworkSetup.java</p>
 * <p>Description: A Helper class that allows an easy creation of a network setup.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package mockObject;

import org.apache.log4j.Logger;

import bcoop.network.p2p.P2PNetwork;
import bcoop.network.p2p.advertisement.PeerAdvertisement;
import bcoop.util.Configuration;

/**
 * @author pmarches
 *
 */
public class MockNetworkSetup {

	public Configuration configServer;
    public Configuration configClient;
    public P2PNetwork netServer;
    public P2PNetwork netClient;
    public MockRepositoryManager mockRepositoryManagerServer;
    public MockRepositoryManager mockRepositoryManagerClient;
    
    public MockNetworkSetup(){
        try{
            configServer = new Configuration();
            //configServer.setProperty(Configuration.OUR_PEER_ID, SERVER_ID);
            configServer.setProperty(Configuration.OUR_SERVER_PORT, "9701");
            configServer.setProperty(Configuration.NUMBER_TRANSACTION_HISTORY, "100");
    
            configClient = new Configuration();
            //configClient.setProperty(Configuration.OUR_PEER_ID, CLIENT_ID);
            configClient.setProperty(Configuration.OUR_SERVER_PORT, "9702");
            configClient.setProperty(Configuration.BOOT_SERVER_IP, "127.0.0.1");
            configClient.setProperty(Configuration.BOOT_SERVER_PORT, configServer.getProperty(Configuration.OUR_SERVER_PORT));
            configClient.setProperty(Configuration.NUMBER_TRANSACTION_HISTORY, "100");
    
            netServer = new P2PNetwork(MockIdentityManager.SERVER_LOCAL_ID, configServer);
            mockRepositoryManagerServer = new MockRepositoryManager();
            netServer.setBlockReceiverHandler(mockRepositoryManagerServer);
            netServer.setRepositoryManager(mockRepositoryManagerServer);
            netServer.bootNetwork();
    
            netClient = new P2PNetwork(MockIdentityManager.CLIENT_LOCAL_ID, configClient);
            mockRepositoryManagerClient = new MockRepositoryManager();
            netClient.setBlockReceiverHandler(mockRepositoryManagerClient);
            netClient.setRepositoryManager(mockRepositoryManagerClient);
            while(netServer.isAcceptingConnections()==false){
            	Logger.getLogger(getClass()).debug("waiting for server to come online.");
            	Thread.sleep(100);
            }
            netClient.bootNetwork();
            
            PeerAdvertisement unreachableAd = new PeerAdvertisement(MockIdentityManager.UNREACHABLE_PEER_ID);
            unreachableAd.setIpAddress("127.0.0.1");
            unreachableAd.setPort(9703);
            netClient.getPeerManager().addPeer(unreachableAd);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void shutdownClientAndServer(){
		netServer.shutdownNetwork();
		netClient.shutdownNetwork();
    }

	public void waitForPeers() {
        netServer.getPeerManager().waitForPeer(1);
        netClient.getPeerManager().waitForPeer(1);
	}

}

/**
 * <p>Title: BCoopServer.java</p>
 * <p>Description: Main server class. Ties all the resources togheter.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import bcoop.backup.scheduler.SchedulerArchiver;
import bcoop.backup.scheduler.ScheduledJob;
import bcoop.block.HeaderBlock;
import bcoop.blocktracker.BlockTrackerArchiver;
import bcoop.bootfile.BootFile;
import bcoop.bootfile.BootFileConverter;
import bcoop.exception.MissingConfigurationException;
import bcoop.exception.NoLocalIdentityDefinedException;
import bcoop.exception.StorageLimitException;
import bcoop.identity.IdentityGenerator;
import bcoop.identity.IdentityManager;
import bcoop.identity.LocalIdentity;
import bcoop.network.Network;
import bcoop.network.PeerInformation;
import bcoop.network.PeerManager;
import bcoop.network.p2p.P2PNetwork;
import bcoop.network.p2p.advertisement.AdvertisementManager;
import bcoop.repos.FSRepositoryManager;
import bcoop.restoration.BackupDiscovery;
import bcoop.restoration.RestoreHeaderJob;
import bcoop.restoration.RestoreJob;
import bcoop.util.BitCoopFile;
import bcoop.util.Configuration;
import bcoop.util.HashMatrix;
import bcoop.worker.BackupWorker;
import bcoop.worker.ClientWorker;
import bcoop.worker.RestoreWorker;

/**
 * @author pmarches
 * 
 */
public class BCoopServer extends BCoopServerBase {
	private static final String IDENTITY_MANAGER_PATH = "identityManager.dat";
	String baseDir;
	public volatile boolean keepRunning = true;
	private ClientWorker clientWorker;
	private Configuration config;

	public BCoopServer(IdentityManager identityManager, Configuration config, Network network) throws UnknownHostException, IOException, MissingConfigurationException, NoLocalIdentityDefinedException {
		this.config = config;
		this.identityManager = identityManager;
		this.baseDir = config.getProperty(Configuration.BASE_DIR);
		if (this.baseDir == null) {
			throw new MissingConfigurationException("BCoopServer must have a baseDir property set");
		}

		this.network = network;
		int transactionHistorySize = 0;
		try {
			if (this.network.getRepositoryManager() != null) { // By default we take the network's repositoryManager
				this.reposManager = this.network.getRepositoryManager();
			} else {
				this.reposManager = new FSRepositoryManager(config);
				this.network.setRepositoryManager(reposManager); // FIXME Maybe merge these two calls
				this.network.setBlockReceiverHandler(reposManager);
			}
			
			transactionHistorySize = Integer.parseInt((String) config.getProperty(Configuration.NUMBER_TRANSACTION_HISTORY));
			String trackerFilename = BlockTrackerArchiver.getTrackerFilename(config);
			this.blockTracker = BlockTrackerArchiver.loadTracker(transactionHistorySize, this.network, new FileInputStream(trackerFilename));
		} catch (MissingConfigurationException e) {
			loadEmptyBlockTracker(transactionHistorySize);
		} catch (FileNotFoundException e) {
			loadEmptyBlockTracker(transactionHistorySize);
		} catch (StorageLimitException e) {
			loadEmptyBlockTracker(transactionHistorySize);
		}
		SchedulerArchiver scheduleArchiver = new SchedulerArchiver(config);
		this.jobScheduler = scheduleArchiver.loadScheduler();

		this.backupWorker = new BackupWorker(this.identityManager.getLocalIdentity(), config, network, network.getPeerManager(), blockTracker, this.jobScheduler, this.reposManager);
		this.restoreWorker = new RestoreWorker(this.network, this.blockTracker, this.identityManager.getLocalIdentity().getEncryptionKey());

		this.clientWorker = new ClientWorker(config, this);
	}

	private void loadEmptyBlockTracker(int transactionHistorySize) throws IOException {
		this.blockTracker = BlockTrackerArchiver.loadTracker(transactionHistorySize, this.network, null);
	}
	
	public void start() throws IOException {
		this.backupWorker.start();
		this.restoreWorker.start();
		this.network.bootNetwork();
		this.clientWorker.start();
	}

	public void shutdown() {
		this.backupWorker.shutdown();
		this.restoreWorker.shutdown();
		this.network.shutdownNetwork();
		this.clientWorker.shutdown();
	}

	/**
	 * @param job
	 */
	public void scheduleJob(ScheduledJob job) {
		backupWorker.scheduleJob(job);
	}

	class ShutDownHook extends Thread {
		public void run() {
			keepRunning = false;
			network.shutdownNetwork();
			
			String trackerFilename=null;
			try {
				trackerFilename = BlockTrackerArchiver.getTrackerFilename(config);
				BlockTrackerArchiver.saveBlockTracker(blockTracker, new FileOutputStream(trackerFilename));
			} catch (MissingConfigurationException e) {
				Logger.getLogger(getClass()).error("Missing tracker file path where to save block tracker");
			} catch (FileNotFoundException e) {
				Logger.getLogger(getClass()).error("Unable to save block tracker to file "+ trackerFilename);
			}
			reposManager.closeAllRepository();
		}
	}

	public static Configuration loadConfiguration(String[] argv) throws IOException, MissingConfigurationException {
		Configuration config = new Configuration("bcoop.xml");
		config.loadLocalSettings();
		DOMConfigurator.configure(config.getLog4JElement());
		
		for (String arg : argv) {
			int index = arg.indexOf("=");
			if (index <= 0){
				continue;
			}

			String key = arg.substring(0, index);
			String value = arg.substring(index + 1);
			Logger.getLogger(BCoopServer.class).info("Overriding " + key + ":" + value);
			config.setProperty(key, value);
		}
		return config;
	}

	public static void main(String[] argv) {
		try {
			Configuration config = loadConfiguration(argv);

			try{
				config.getProperty(Configuration.BOOTFILE_PATH); //This will throw if the config file path was not given as argument
				if (argv[0].equalsIgnoreCase("-create")) {
					createBootfile(config);
				}
				else{
					restoreSystem(config);
				}
			}
			catch(MissingConfigurationException e){
				startServer(config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createBootfile(Configuration config) throws IOException, MissingConfigurationException, NoLocalIdentityDefinedException{
		IdentityManager identityManager = new IdentityManager(IDENTITY_MANAGER_PATH);
		LocalIdentity localIdentity = identityManager.getLocalIdentity();
		Network network = new P2PNetwork(localIdentity, config);
		PeerManager pMan = network.getPeerManager();

		String bootFilePath = config.getProperty(Configuration.BOOTFILE_PATH);
		Logger.getLogger(BCoopServer.class).debug("Creating bootfile "+ bootFilePath);
		BitCoopFile destinationFile = new BitCoopFile(bootFilePath);
		BootFileConverter.exportBootFile(destinationFile, localIdentity, (AdvertisementManager) pMan);
	}

	private static void restoreSystem(Configuration config) throws UnknownHostException, IOException, MissingConfigurationException, NoLocalIdentityDefinedException {
		//Check presence of arguments..
		config.getProperty(Configuration.RESTORE_PATH);
		
		// Load the bootfile
		BootFile bFile = new BootFile();
		bFile.readFromStream(new ObjectInputStream(new FileInputStream(config.getProperty(Configuration.BOOTFILE_PATH))));

		// Create the server
		Network network = new P2PNetwork(bFile.getLocalIdentity(), config);
		IdentityManager identityManager = new IdentityManager(IDENTITY_MANAGER_PATH);
		identityManager.setLocalIdentity(bFile.getLocalIdentity());
		BCoopServer server = new BCoopServer(identityManager, config, network);

		// Load the peers
		PeerManager pMan = network.getPeerManager();
		for (PeerInformation peerInfo : bFile.getAllPeers()) {
			pMan.addPeer(peerInfo);
		}
		server.start();

		// Get the header blocks
		BackupDiscovery backupDisc = new BackupDiscovery(pMan, network);
		HashMatrix discoveredHeaderBlocks = backupDisc.discoverHeaderBlocks();

		String restoreToDirectory = config.getProperty(Configuration.RESTORE_PATH);
		// Create restore job
		Set<String> peerNames = discoveredHeaderBlocks.getColumnIndex();
		for(String peer : peerNames){
			Hashtable<String, Object> files = discoveredHeaderBlocks.getColumn(peer);
			for(String filename : files.keySet()){
				HeaderBlock hBlockToRestore = (HeaderBlock) discoveredHeaderBlocks.get(peer, filename);
				RestoreJob rJob = new RestoreHeaderJob(hBlockToRestore, restoreToDirectory);
				//TODO filter out the files we do not want to have restored..
				server.restoreWorker.addToRestoreQueue(rJob);
			}
		}

	}

	private static void startServer(Configuration config) throws UnknownHostException, IOException, MissingConfigurationException, NoLocalIdentityDefinedException {
		IdentityManager identityManager = new IdentityManager(IDENTITY_MANAGER_PATH);
		LocalIdentity localIdentity;
		try {
			localIdentity = identityManager.getLocalIdentity();
		} catch (NoLocalIdentityDefinedException e) {
			String humanReadableAlias = config.getProperty(Configuration.OUR_SERVER_ALIAS);
			Logger.getLogger(BCoopServer.class).info("Generating LocalIdentity for alias "+humanReadableAlias);
			IdentityGenerator idGen = new IdentityGenerator(null);
			localIdentity = idGen.generateLocalIdentity(humanReadableAlias);
			identityManager.setLocalIdentity(localIdentity);
		}
		Logger.getLogger(BCoopServer.class).info("PeerId:" + localIdentity.getHumanReadableAlias());
		Network network = new P2PNetwork(localIdentity, config);
		BCoopServer server = new BCoopServer(identityManager, config, network);

		Runtime.getRuntime().addShutdownHook(server.new ShutDownHook());
		server.start();
	}

}

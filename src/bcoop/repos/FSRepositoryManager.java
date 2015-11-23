/**
 * <p>Title: FSRepositoryManager.java</p>
 * <p>Description: Filesystem implementation of the repository manager</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.repos;

import java.io.File;
import java.io.FileFilter;
import java.security.PublicKey;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;

import bcoop.exception.MissingConfigurationException;
import bcoop.exception.StorageLimitException;
import bcoop.identity.Identity;
import bcoop.util.BitCoopFile;
import bcoop.util.Configuration;
import bcoop.util.ConfigurationNode;
import bcoop.util.SuffixedNumber;

/**
 * @author pmarches
 *
 */
public class FSRepositoryManager extends RepositoryManager {
	BitCoopFile repositoryDirectory;
	RepositoryFilter repoFilter = new RepositoryFilter();
	private Configuration config;
	
	class RepositoryFilter implements FileFilter{
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	}
	
	public FSRepositoryManager(Configuration config) throws MissingConfigurationException, StorageLimitException{
		this.config = config;
		long globalSpace = parseLimit(config.getProperty(Configuration.GLOBAL_ALLOWED_SPACE));
		setGlobalMaximumAllowedSpace(globalSpace);
		
		ConfigurationNode repositoryConfig = config.getConfigElement("bcoop.repository");
		Hashtable<String, Long> allowedSpacePerPeer = new Hashtable<String, Long>();
		for(ConfigurationNode allowedSpace : repositoryConfig.getChilds("allowedSpace")){
			allowedSpacePerPeer.put(allowedSpace.getAttribute("name"), parseLimit(allowedSpace.getElementValue()));
		}
		long defaultAllowedLimit = parseLimit(config.getProperty("bcoop.repository.defaultLimitPerPeer"));
		
		String directory = config.getProperty(Configuration.BASE_DIR)+"/repos";
		repositoryDirectory = new BitCoopFile(directory);
		if(!repositoryDirectory.exists()){
			Logger.getLogger(RepositoryManager.class).info(repositoryDirectory.getAbsolutePath()+" does not exist, creating it.");
			repositoryDirectory.mkdirs();
		}
		peerRepositories = new Hashtable<Identity, BlockRepository>();
		BitCoopFile[] peerRepos = repositoryDirectory.listFiles(repoFilter);
		for(int i=0; i<peerRepos.length; i++){
			Long peerKeyId = Long.parseLong(peerRepos[i].getName());
			Identity peerId = new Identity(peerKeyId, null, (PublicKey) null);
			
			Logger.getLogger(this.getClass()).info("Loading repository:" + peerId.getUniqueId());
			BlockRepository repo = new FSBlockRepository(this, peerRepos[i].getAbsolutePath());
			if(allowedSpacePerPeer.containsKey(peerKeyId)){
				repo.setAllowedSpace(allowedSpacePerPeer.get(peerKeyId));
			}
			else{
				repo.setAllowedSpace(defaultAllowedLimit);
			}
			registerRepository(peerId, repo);
		}
	}
	
	private Long parseLimit(String stringLimit) {
		if(stringLimit.equalsIgnoreCase("UNLIMITED")){
			return RepositoryManager.UNLIMITED_SPACE_ALLOWED;
		}
		else{
			return SuffixedNumber.fromString(stringLimit);
		}
	}
	
	public BlockRepository createBlockRepository(Identity peerId) {
		try {
			BlockRepository newRepo = new FSBlockRepository(this, repositoryDirectory.getAbsolutePath()+"/"+peerId.getUniqueId());
			long defaultAllowedLimit = parseLimit(this.config.getProperty("bcoop.repository.defaultLimitPerPeer"));
			newRepo.setAllowedSpace(defaultAllowedLimit);
			return newRepo;
		} catch (MissingConfigurationException e) {
			e.printStackTrace();
		} catch (StorageLimitException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void closeAllRepository() {
		Iterator<BlockRepository> it = peerRepositories.values().iterator();
		while(it.hasNext()){
			FSBlockRepository repos = (FSBlockRepository) it.next();
			repos.saveIndex();
		}
	}
}

/**
 * <p>Title: MockRepositoryManager.java</p>
 * <p>Description: A Mock Repository manager userfull for testing.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package mockObject;

import bcoop.identity.Identity;
import bcoop.repos.BlockRepository;
import bcoop.repos.RepositoryManager;


/**
 * @author pmarches
 *
 */
public class MockRepositoryManager extends RepositoryManager {
	public MockRepositoryManager(){
		setGlobalMaximumAllowedSpace(RepositoryManager.UNLIMITED_SPACE_ALLOWED);
	}
	
    protected BlockRepository createBlockRepository(Identity peerId) {
        return new MockBlockRepository();
    }

    public void closeAllRepository() {
    }

}

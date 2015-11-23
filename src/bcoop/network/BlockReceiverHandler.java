/**
 * <p>Title: BlockRequestHandler.java</p>
 * <p>Description: Interface to receive blocks into a repository.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network;

import bcoop.block.NumberedBlock;
import bcoop.exception.StorageLimitException;
import bcoop.identity.Identity;

/**
 * @author pmarches
 *
 */
public interface BlockReceiverHandler {
	public long handleReceivedBlock(Identity sourcePeerId, NumberedBlock newBlock) throws StorageLimitException;
}

/**
 * <p>Title: Advertisement.java</p>
 * <p>Description: Basic advertisements sent to other hosts.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.advertisement;

import java.io.Serializable;

/**
 * @author pmarches
 *
 */
public interface Advertisement extends Serializable, Cloneable {
	public long getExpiration();
	public int getHopCount();
}

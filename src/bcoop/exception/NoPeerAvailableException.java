/**
 * <p>Title: NoPeerAvailableException.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.exception;

/**
 * @author pmarches
 *
 */
public class NoPeerAvailableException extends Exception {
	private static final long serialVersionUID = -2816287039457604797L;

	public NoPeerAvailableException(){
		super("No peers available..");
	}
}

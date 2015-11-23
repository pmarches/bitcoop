package bcoop.exception;

public class NoLocalIdentityDefinedException extends Exception {
	private static final long serialVersionUID = 6554002125603203766L;

	public NoLocalIdentityDefinedException(){
		super("LocalIdentity should not be null");
	}
}

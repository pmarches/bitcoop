package bcoop.exception;

import java.io.IOException;

public class StreamClosedException extends IOException {

	private static final long serialVersionUID = 2291573288184666476L;
	public StreamClosedException(){
		super("Stream closed");
	}
}

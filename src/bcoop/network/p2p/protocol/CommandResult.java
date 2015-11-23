package bcoop.network.p2p.protocol;

import java.io.Serializable;

public class CommandResult implements Serializable{
	private static final long serialVersionUID = -9210703163676504194L;

	static final CommandResult OK_RESULT = new CommandResult();
	Exception exceptionThrown;
	int errorCode;
	String errorMsg;
	long longValue;
	
	public CommandResult(int errorCode, String errorMsg){
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public CommandResult(Exception e){
		this.exceptionThrown = e;
	}
	
	public CommandResult() {
		this.errorCode = 0;
	}

	public boolean hasErrorOccured(){
		return errorCode != 0 || exceptionThrown != null;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Exception getExceptionThrown() {
		return exceptionThrown;
	}

	public void setExceptionThrown(Exception exceptionThrown) {
		this.exceptionThrown = exceptionThrown;
	}

	public void setLong(long longValue) {
		this.longValue = longValue;
	}
	public long getLong() {
		return this.longValue;
	}
	
	
}

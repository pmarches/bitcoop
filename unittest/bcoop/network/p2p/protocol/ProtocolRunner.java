/**
 * <p>Title: ProtocolRunner.java</p>
 * <p>Description: Executes the protocols for client or server</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.network.p2p.protocol;


class ProtocolRunner extends Thread{
	Protocol prot;
	private boolean isClient;

	public ProtocolRunner(Protocol prot, boolean isClient){
		super("ProtocolRunner "+(isClient?"client":"server"));
		this.prot = prot;
		this.prot.setReadRemoteType(true);
		this.isClient = isClient;
	}
	
	public void run(){
		try{
			if(isClient) this.prot.executeClient();
			else this.prot.executeServer();
		}
		catch(Exception e){
			e.printStackTrace();
			PeerExchangeProtocolTest.fail();
		}
	}
}
package bcoop.network;

import java.io.IOException;

public interface VisitorAction {
	public void visit(PeerInformation peerInfo) throws IOException;
}

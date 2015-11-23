/**
 * <p>Title: MockNetwork.java</p>
 * <p>Description: Bogus implementation of a network</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package mockObject;

import java.io.IOException;

import bcoop.identity.Identity;
import bcoop.network.Connection;
import bcoop.network.Network;

/**
 * @author pmarches
 *
 */
public class MockNetwork extends Network {

    public Connection getConnection(Identity peerId) {
        throw new MockException();
    }

    public void bootNetwork() throws IOException {
        throw new MockException();
    }

    public void shutdownNetwork() {
        throw new MockException();
    }

}

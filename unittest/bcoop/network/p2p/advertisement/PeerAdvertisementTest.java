package bcoop.network.p2p.advertisement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;

import bcoop.identity.Identity;
import junit.framework.TestCase;

public class PeerAdvertisementTest extends TestCase {

	/*
	 * Test method for 'bcoop.network.p2p.advertisement.PeerAdvertisement.writeTo(BCoopOutputStream)'
	 */
	public void testWriteTo() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream arrayStream = new ByteArrayOutputStream();
		ObjectOutputStream bOut = new ObjectOutputStream(arrayStream);
		PeerAdvertisement peerAd = new PeerAdvertisement(new Identity(0x1123, "dkdk", (PublicKey) null));
		bOut.writeObject(peerAd);
		
		ObjectInputStream bIn = new ObjectInputStream(new ByteArrayInputStream(arrayStream.toByteArray()));
		PeerAdvertisement peerAdRead = (PeerAdvertisement) bIn.readObject();
		assertEquals(0, bIn.available());
		assertEquals(peerAd, peerAdRead);
	}

}

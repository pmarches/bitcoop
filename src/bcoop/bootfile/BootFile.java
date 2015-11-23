package bcoop.bootfile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import bcoop.identity.Identity;
import bcoop.identity.LocalIdentity;
import bcoop.network.p2p.advertisement.PeerAdvertisement;
import bcoop.util.BitCoopFile;

public class BootFile {
	private static final String BOOTFILE_TAG = "bootfile";
	private static final String BOOTFILE_TAG_ALIAS = "alias";
	private static final String BOOTFILE_TAG_ID = "id";
	private static final String KEYRING_TAG = "keyring";
	private static final String PRIVATE_KEY_TAG = "secretKey";
	private static final String PUBLIC_KEY_TAG = "publicKey";
	private static final String SYMETRIC_KEY_TAG = "symetricKey";
	private static final String PEER_LIST_TAG = "peerList";
	private static final String PEER_TAG = "peer";
	private static final String PEER_TAG_ID = "id";
	private static final String PEER_TAG_ALIAS = "alias";
	private static final String PEER_TAG_IP = "ip";
	private static final String PEER_TAG_PORT = "port";
	//A list of possible peers
	//Private and Public key
	//Name of this peer
	private LocalIdentity peerIdentity;
	private Vector<PeerAdvertisement> savedPeersIpAndPorts = new Vector<PeerAdvertisement>();
	static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public LocalIdentity getLocalIdentity() {
		return peerIdentity;
	}
	public void setLocalIdentity(LocalIdentity peerIdentity) {
		this.peerIdentity = peerIdentity;
	}
	public void addPeer(PeerAdvertisement currentPeer) {
		savedPeersIpAndPorts.add(currentPeer);
	}
	
	public void saveToFile(BitCoopFile exportFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(exportFile);
		saveToStream(fos);
		fos.close();
	}
	
	public Document getXMLDocument() throws ParserConfigurationException, DOMException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element bootFileElement = doc.createElement(BOOTFILE_TAG);
		bootFileElement.setAttribute("id", this.peerIdentity.getUniqueIdString());
		bootFileElement.setAttribute("alias", this.peerIdentity.getHumanReadableAlias());
		doc.appendChild(bootFileElement);
		
		Element keyringElement = doc.createElement(KEYRING_TAG);
		bootFileElement.appendChild(keyringElement);
		
		Element secretKeyElement = doc.createElement(PRIVATE_KEY_TAG);
		BASE64Encoder b64Encoder = new BASE64Encoder();
		secretKeyElement.setTextContent(b64Encoder.encode(this.peerIdentity.getPEMPrivateKeyBytes()));
		keyringElement.appendChild(secretKeyElement);

		Element publicKeyElement = doc.createElement(PUBLIC_KEY_TAG);
		publicKeyElement.setTextContent(b64Encoder.encode(this.peerIdentity.getPEMPublicKeyBytes()));
		keyringElement.appendChild(publicKeyElement);

		Element symetricKeyElement = doc.createElement(SYMETRIC_KEY_TAG);
		symetricKeyElement.setTextContent(b64Encoder.encode(this.peerIdentity.getPEMSymmetricKeyBytes()));
		keyringElement.appendChild(symetricKeyElement);

		Element peerListElement = doc.createElement(PEER_LIST_TAG);
		bootFileElement.appendChild(peerListElement);

		for(PeerAdvertisement peerAd : this.savedPeersIpAndPorts){
			Element peerElement = doc.createElement(PEER_TAG);
			peerElement.setAttribute(PEER_TAG_ID, peerAd.getPeerId().getUniqueIdString());
			peerElement.setAttribute(PEER_TAG_ALIAS, peerAd.getPeerId().getHumanReadableAlias());
			peerElement.setAttribute(PEER_TAG_IP, peerAd.getIpAddress());
			peerElement.setAttribute(PEER_TAG_PORT, Integer.toString(peerAd.getPort()));

			byte[] peerPublicBytes = peerAd.getPeerId().getPEMPublicKeyBytes();
			if(peerPublicBytes != null){
				Element peerPublicKeyElement = doc.createElement(PUBLIC_KEY_TAG);
				peerPublicKeyElement.setTextContent(b64Encoder.encode(peerPublicBytes));
				peerElement.appendChild(peerPublicKeyElement);
			}
			
			peerListElement.appendChild(peerElement);
		}

		return doc;
	}

	public void saveToStream(OutputStream exportStream) throws IOException{
		try{
			Document doc = getXMLDocument();
			
		    TransformerFactory transFactory = TransformerFactory.newInstance(); 
	    	Transformer trans = transFactory.newTransformer();
	    	trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	    	trans.setOutputProperty(OutputKeys.INDENT, "yes");
	    	trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    	
		    Source src = new DOMSource(doc); 
		    Result dest = new StreamResult(exportStream); 
		    trans.transform(src, dest);
		}
		catch(Exception e){
			e.printStackTrace();
			Logger.getLogger(this.getClass()).error(e);
		}
	}

	public void readFromStream(InputStream importStream) throws IOException{
		try{
			BASE64Decoder b64Decoder = new BASE64Decoder();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(importStream);

			Element bootFileElement = (Element) dom.getElementsByTagName(BOOTFILE_TAG).item(0);
			Element keyringElement = (Element) bootFileElement.getElementsByTagName(KEYRING_TAG).item(0);
			Element privateKeyElement = (Element) keyringElement.getElementsByTagName(PRIVATE_KEY_TAG).item(0);
			byte[] privatePEMKey = b64Decoder.decodeBuffer(privateKeyElement.getTextContent());
			
			Element publicKeyElement = (Element) keyringElement.getElementsByTagName(PUBLIC_KEY_TAG).item(0);
			byte[] publicPEMKey = b64Decoder.decodeBuffer(publicKeyElement.getTextContent());

			Element symetricKeyElement = (Element) keyringElement.getElementsByTagName(SYMETRIC_KEY_TAG).item(0);
			byte[] symetricPEMKey = b64Decoder.decodeBuffer(symetricKeyElement.getTextContent());
			
			long thisPeerUniqueId = new BigInteger(bootFileElement.getAttribute(BOOTFILE_TAG_ID), 16).longValue();
			this.peerIdentity = new LocalIdentity(thisPeerUniqueId, bootFileElement.getAttribute(BOOTFILE_TAG_ALIAS), publicPEMKey, privatePEMKey, symetricPEMKey);
			
			Element peerListElement = (Element) bootFileElement.getElementsByTagName(PEER_LIST_TAG).item(0);
			NodeList nodePeerList = peerListElement.getChildNodes();
			for(int i=0; i<nodePeerList.getLength(); i++){
				Node currentNode = (Node) nodePeerList.item(i);
				if(currentNode.getNodeType()==Node.ELEMENT_NODE){
					Element peer = (Element) currentNode;
					Element peerPublicKey = (Element) peer.getElementsByTagName(PUBLIC_KEY_TAG).item(0);
					byte[] PEMBytes = null;
					if(peerPublicKey != null){
						PEMBytes = b64Decoder.decodeBuffer(peerPublicKey.getTextContent());
					}
					
					long peerUniqueId = new BigInteger(peer.getAttribute(PEER_TAG_ID), 16).longValue();
					Identity identity = new Identity(peerUniqueId, peer.getAttribute(PEER_TAG_ALIAS), PEMBytes);
					PeerAdvertisement peerAd = new PeerAdvertisement(identity);
					peerAd.setIpAddress(peer.getAttribute(PEER_TAG_IP));
					peerAd.setPort(Integer.parseInt(peer.getAttribute(PEER_TAG_PORT)));
					
					this.savedPeersIpAndPorts.add(peerAd);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
		
	public PeerAdvertisement[] getAllPeers() {
		PeerAdvertisement[] retVal = new PeerAdvertisement[this.savedPeersIpAndPorts.size()];
		this.savedPeersIpAndPorts.toArray(retVal);
		return retVal;
	}
	
	public boolean equals(Object obj){
		if(! (obj instanceof BootFile)){
			return false;
		}
		BootFile otherBFile = (BootFile) obj;
		if(this.peerIdentity.equals(otherBFile.peerIdentity)==false){
			return false;
		}
		if(this.savedPeersIpAndPorts.equals(otherBFile.savedPeersIpAndPorts) == false){
			return false;
		}
		return true;
	}
}

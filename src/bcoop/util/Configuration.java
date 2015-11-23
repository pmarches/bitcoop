/**
 * <p>Title: Configuration.java</p>
 * <p>Description: Configuration class </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bcoop.exception.MissingConfigurationException;

/**
 * @author pmarches
 *
 */
public class Configuration{
	private static final long serialVersionUID = 4649215062048412541L;
	public static final String OUR_SERVER_ALIAS = "bcoop.server.ourPeerId";
	public static final String OUR_SERVER_PORT = "bcoop.server.ourServerPort";
	public static final String BASE_DIR = "bcoop.baseDir";
	public static final String BOOT_SERVER_PORT = "bcoop.bootserver.port";
	public static final String BOOT_SERVER_IP = "bcoop.bootserver.ip";
	public static final String CLIENT_PORT = "bcoop.clientPort";
	public static final String XML_FILE = "bcoop.xmlFile";
	public static final String GLOBAL_ALLOWED_SPACE = "bcoop.repository.globalLimit";
	public static final String DEFAULT_ALLOWED_PER_PEER = "bcoop.repository.defaultLimitPerPeer";
	public static final String BOOTFILE_PATH = "bcoop.bootfilepath";
	public static final String RESTORE_PATH = "bcoop.restorepath";
	public static final String AD_MAN_FILE = "bcoop.admanpath";
	public static final String NUMBER_TRANSACTION_HISTORY = "bcoop.numberTransactionHistory";
	public static final String BITCOOP_CONFIG_FILENAME="bcoop.xml";
	private static final String BITCOOP_CONFIG_LOCAL_FILENAME = BITCOOP_CONFIG_FILENAME+".local";
	
	protected Document dom;
	protected Document localDom;
	
	static DocumentBuilderFactory factory;
	static DocumentBuilder builder;
	
	static{
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public Configuration(String filename) throws IOException{
		loadConfigFromXML(new FileInputStream(filename));
	}
	
	public Configuration(InputStream stream) throws IOException{
		loadConfigFromXML(stream);
	}
	
	public Configuration() {
		this.dom = builder.newDocument();
		this.dom.appendChild(this.dom.createElement("bcoop"));
	}

	protected void loadConfigFromXML(InputStream xmlInput){
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			dom = builder.parse(xmlInput);
			loadSystemProperties();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void loadSystemProperties() {
		try {
			ConfigurationNode systemPropsElement = getConfigElement("bcoop.systemProperties");
			Vector<ConfigurationNode> props = systemPropsElement.getChilds(null);
			for(ConfigurationNode systemProperty: props){
				System.setProperty(systemProperty.getElementName(), systemProperty.getElementValue());
			}
		} catch (MissingConfigurationException e) {
		}
		
	}

	public ConfigurationNode getConfigElement(String path) throws MissingConfigurationException{
		try{
			if(this.localDom == null){
				throw new MissingConfigurationException();
			}
			return new ConfigurationNode(getXMLElement(path, this.localDom));
		}
		catch(MissingConfigurationException e){
			return new ConfigurationNode(getXMLElement(path, this.dom));
		}
	}
	
	protected Element getXMLElement(String path, Document domToQuery) throws MissingConfigurationException{
		Element currentElement = domToQuery.getDocumentElement();
		StringTokenizer tok = new StringTokenizer(path, ".");
		if(tok.nextToken().equalsIgnoreCase(currentElement.getNodeName())==false){
			throw new MissingConfigurationException("Key "+path+" was not found in the configuration");
		}
		while(tok.hasMoreTokens()){
			String pathElement = tok.nextToken();
			NodeList elementList = currentElement.getElementsByTagName(pathElement);
			if(elementList != null && elementList.getLength() == 0){
				throw new MissingConfigurationException("Key "+path+" was not found in the configuration");
			}
			currentElement = (Element) elementList.item(0);
		}
		return currentElement;
	}
	
	public String getProperty(String path) throws MissingConfigurationException {
		Element elem = null;
		try{
			if(this.localDom == null){
				throw new MissingConfigurationException();
			}
			elem = getXMLElement(path, this.localDom);
		}
		catch(MissingConfigurationException e){
			elem = getXMLElement(path, this.dom);
		}
		elem.normalize();
		return elem.getTextContent().trim();
	}

	public void setProperty(String path, String value) {
		Element createdElement = createElementFromPath(path);
		createdElement.setTextContent(value);
	}

	private Element createElementFromPath(String path) {
		Element createdElement = dom.getDocumentElement();
		StringTokenizer tok = new StringTokenizer(path, ".");
		String currentElementName = tok.nextToken();
		if(currentElementName.equalsIgnoreCase(createdElement.getNodeName())==false){
			createdElement = dom.createElement(currentElementName);
			dom.adoptNode(createdElement);
		}
		while(tok.hasMoreTokens()){
			currentElementName = tok.nextToken();
			NodeList childList = createdElement.getElementsByTagName(currentElementName);
			if(childList == null || childList.getLength() == 0){
				Element newElement = dom.createElement(currentElementName);
				createdElement.appendChild(newElement);
				createdElement = newElement;
			}
			else{
				createdElement = (Element) childList.item(0);
			}
		}
		return createdElement;
	}

	public String getProperty(String path, String defaultValue) {
		try {
			String value = getProperty(path);
			return value;
		} catch (MissingConfigurationException e) {
			return defaultValue;
		}
	}

	public ConfigurationNode newConfigNode(String nodePath) {
		Element createdElement = createElementFromPath(nodePath);
		return new ConfigurationNode(createdElement);
	}
	
	public void loadLocalSettings() throws FileNotFoundException{
		File localFileName = new File(BITCOOP_CONFIG_LOCAL_FILENAME);
		if(localFileName.canRead()){
			loadLocalSettings(localFileName.getAbsolutePath());
		}
	}

	public void loadLocalSettings(String localFilename) throws FileNotFoundException{
		//TODO should we log here instead ?
		System.out.println("Loading local configuration from file "+localFilename);
		loadLocalSettings(new FileInputStream(localFilename));
	}

	public void loadLocalSettings(InputStream localStream){
		try{
			localDom = builder.parse(localStream);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public Element getLog4JElement() throws MissingConfigurationException {
		if(localDom != null){
			try{
				return getXMLElement("bcoop.log4j:configuration", localDom);
			}
			catch(MissingConfigurationException mce){
				
			}
		}
		return getXMLElement("bcoop.log4j:configuration", dom);
	}
}

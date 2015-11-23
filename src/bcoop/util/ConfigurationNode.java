package bcoop.util;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigurationNode {
	private Element xmlElement;
	
	ConfigurationNode(Element xmlElement){
		this.xmlElement = xmlElement;
	}
	
	public String getElementValue(){
		return xmlElement.getTextContent();
	}
	
	public void setElementValue(String value){
		this.xmlElement.setTextContent(value);
	}
	
	public Vector<ConfigurationNode> getChilds(String elementName){
		Vector<ConfigurationNode> configElementList = new Vector<ConfigurationNode>();
		
		NodeList xmlChilds = this.xmlElement.getChildNodes();
		for(int i=0; i<xmlChilds.getLength(); i++){
			Node node = xmlChilds.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				if(elementName == null || node.getNodeName().equalsIgnoreCase(elementName)){
					configElementList.add(new ConfigurationNode((Element) node));
				}
			}
		}
		return configElementList;
	}

	public String getElementName() {
		return this.xmlElement.getNodeName();
	}

	public Element getXMLElement() {
		return this.xmlElement;
	}

	public String getAttribute(String attributeName) {
		return this.xmlElement.getAttribute(attributeName);
	}

	public void setAttribute(String attrName, String attrValue) {
		this.xmlElement.setAttribute(attrName, attrValue);
	}
}

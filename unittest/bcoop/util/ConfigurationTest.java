/**
 * <p>Title: ConfigurationTest.java</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import bcoop.exception.MissingConfigurationException;

import junit.framework.TestCase;

/**
 * @author pmarches
 *
 */
public class ConfigurationTest extends TestCase {
	String testConfiguration = "<bcoop>"+
		"<someValue>value</someValue>"+
		"<emptyValue></emptyValue>\n<systemProperties><some.prop>toto</some.prop></systemProperties>"+
		"</bcoop>\n";
	Configuration config;
	
	public void setUp(){
		try{
			config = new Configuration(new ByteArrayInputStream(testConfiguration.getBytes()));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void testReadProperty(){
		try{
			assertEquals("value", config.getProperty("bcoop.someValue"));
			assertEquals("", config.getProperty("bcoop.emptyValue"));
			assertNull(config.getProperty("Non-existing key...", null));

			try{
				config.getProperty("Bogus Key");
				fail();
			}
			catch(MissingConfigurationException e){
				assertTrue(true);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	public void testWrite(){
		try{
			config.setProperty("bcoop.myNewValue", "newValue");
			assertEquals("newValue", config.getProperty("bcoop.myNewValue"));
		}
		catch(MissingConfigurationException e){
			fail();
		}
	}
	
	public void testXML(){
		try {
			config.loadConfigFromXML(new FileInputStream("bcoop.xml"));
			String baseDirElement = config.getProperty(Configuration.BASE_DIR);
			assertEquals("/tmp/bcoop", baseDirElement);
			try{
				config.getProperty("bcoop.invalid");
				fail();
			}
			catch(MissingConfigurationException e){
				//Ok.
			}
			assertEquals(3, config.getConfigElement("bcoop.repository").getChilds("allowedSpace").size());

			assertEquals("UNLIMITED", config.getProperty("bcoop.repository.defaultLimitPerPeer"));
			
			assertEquals("toto", System.getProperty("some.prop"));
			ConfigurationNode newNode = config.newConfigNode("bcoop.some.node");
			newNode.setAttribute("myAttr", "myValue");
			assertEquals("myValue", newNode.getAttribute("myAttr"));
			assertEquals("myValue", config.getConfigElement("bcoop.some.node").getAttribute("myAttr"));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (MissingConfigurationException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testLoadLocalXML(){
		try {
			config.loadConfigFromXML(new FileInputStream("bcoop.xml"));
			ConfigurationNode filesetNode = config.getConfigElement("bcoop.backup");
			assertEquals(7, filesetNode.getChilds("fileset").size());

			config.loadLocalSettings();
			String baseDirElement = config.getProperty(Configuration.OUR_SERVER_ALIAS);
			assertFalse("".equals(baseDirElement)); //could be anything..
			
			filesetNode = config.getConfigElement("bcoop.backup");
			assertEquals(1, filesetNode.getChilds("fileset").size());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (DOMException e) {
			e.printStackTrace();
			fail();
		} catch (MissingConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		
	}
}

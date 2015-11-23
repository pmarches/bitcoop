/**
 * <p>Title: JobFactory.java</p>
 * <p>Description: Loads jobs andschedules from a XML stream</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import java.util.Vector;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import bcoop.backup.fileselection.ExcludeDataSelector;
import bcoop.backup.fileselection.IncludeDataSelector;
import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.exception.MissingConfigurationException;
import bcoop.util.Configuration;
import bcoop.util.ConfigurationNode;

/**
 * @author pmarches
 */
public class JobFactory {
	Vector<ScheduledJob> scheduledJobList = new Vector<ScheduledJob>();
	Hashtable<String, NamedDataGroup> dataList = new Hashtable<String, NamedDataGroup>();
	Hashtable<String, Schedule> scheduleList = new Hashtable<String, Schedule>();
	
	public JobFactory(Configuration config){
		loadDefaults();

		try{
			ConfigurationNode backupElement = config.getConfigElement("bcoop.backup");
			loadDatas(backupElement);
			loadSchedules(backupElement);
			loadJobs(backupElement);
		}
		catch(MissingConfigurationException e){
		}
	}
	
	public void loadDefaults(){
		scheduleList.put("bootime", new NowSchedule());
		scheduleList.put("always", new Schedule("always"));
	}
	
	/**
	 * @param topLevelNodes
	 */
	private void loadJobs(ConfigurationNode backupElement) {
		Vector<ConfigurationNode> jobArray = backupElement.getChilds("job");

		for(ConfigurationNode jobElement : jobArray){
			ScheduledJob job = loadJob(jobElement.getXMLElement());
			scheduledJobList.add(job);
		}
	}
	
	private ScheduledJob loadJob(Element topLevelNode) {
		ScheduledJob job = new ScheduledJob();
		NodeList dataElements = topLevelNode.getElementsByTagName("fileset");
		for(int j=0; j<dataElements.getLength(); j++){
			Element referencedData = (Element) dataElements.item(j);
			String name = referencedData.getAttribute("name");
			NamedDataGroup data = this.dataList.get(name);
			if(data == null){
				throw new RuntimeException("Invalid data name "+name);
			}
			Logger.getLogger(this.getClass()).debug("Read data group named: "+name);
			job.addNamedDataGroup(data);
		}
		NodeList scheduleElements = topLevelNode.getElementsByTagName("schedule");
		for(int j=0; j<scheduleElements.getLength(); j++){
			Element referencedSchedule = (Element) scheduleElements.item(j);
			String name = referencedSchedule.getAttribute("name");
			Schedule schedule = this.scheduleList.get(name);
			if(schedule == null){
				throw new RuntimeException("Invalid schedule name "+name);
			}
			Logger.getLogger(this.getClass()).debug("Read schedule named: "+name);
			job.addSchedule(schedule);
		}
		return job;
	}
	
	/**
	 * @param topLevelNodes
	 */
	private void loadSchedules(ConfigurationNode backupElement) {
		Vector<ConfigurationNode> scheduleArray = backupElement.getChilds("schedule");

		for(ConfigurationNode scheduleElement : scheduleArray){
			Element topLevelElement = scheduleElement.getXMLElement();
			String name = topLevelElement.getAttribute("name");
			Schedule schedule = new Schedule(name);
			Element child = (Element) topLevelElement.getElementsByTagName("minute").item(0);
			if(child != null) schedule.setMinute(child.getAttribute("value"));
			
			child = (Element) topLevelElement.getElementsByTagName("hour").item(0);
			if(child != null) schedule.setHour(child.getAttribute("value"));
			
			child = (Element) topLevelElement.getElementsByTagName("day").item(0);
			if(child != null) schedule.setDay(child.getAttribute("value"));
			
			child = (Element) topLevelElement.getElementsByTagName("weekday").item(0);
			if(child != null) schedule.setWeekday(child.getAttribute("value"));
			
			child = (Element) topLevelElement.getElementsByTagName("month").item(0);
			if(child != null) schedule.setMonth(child.getAttribute("value"));
			
			scheduleList.put(name, schedule);
		}
	}
	
	/**
	 * @param topLevelNodes
	 */
	private void loadDatas(ConfigurationNode backupElement) {
		Vector<ConfigurationNode> filesetArray = backupElement.getChilds("fileset");
		for(ConfigurationNode currentfileSet : filesetArray){
			NamedDataGroup newData = loadData(currentfileSet.getXMLElement());
			dataList.put(newData.getName(), newData);
		}
	}
	
	/**
	 * @param topLevelNode
	 * @return
	 */
	private NamedDataGroup loadData(Element topLevelNode) {
		String name = topLevelNode.getAttribute("name");
		NamedDataGroup newData = new NamedDataGroup(name);
		NodeList includeElements = topLevelNode.getElementsByTagName("include");
		for(int i=0; i<includeElements.getLength(); i++){
			Element e = (Element) includeElements.item(i);
			newData.addSelector(new IncludeDataSelector(e.getAttribute("pattern"), e.getAttribute("size")));
		}
		NodeList excludeElements = topLevelNode.getElementsByTagName("exclude");
		for(int i=0; i<excludeElements.getLength(); i++){
			Element e = (Element) excludeElements.item(i);
			newData.addSelector(new ExcludeDataSelector(e.getAttribute("pattern"), e.getAttribute("size")));
		}
		
		return newData;
	}

	public Vector<ScheduledJob> getJobs() {
		return scheduledJobList;
	}

	public Vector<Schedule> getSchedule() {
		return new Vector<Schedule>(this.scheduleList.values());
	}

	public Vector<NamedDataGroup> getDataGroup() {
		return new Vector<NamedDataGroup>(this.dataList.values());
	}
}

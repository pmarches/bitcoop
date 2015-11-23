/**
 * <p>Title: ScheduledJob.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import java.io.Serializable;
import java.util.Vector;

import bcoop.backup.fileselection.FileSelection;
import bcoop.backup.fileselection.NamedDataGroup;

/**
 * @author pmarches
 *
 */
public class ScheduledJob implements Serializable{
	private static final long serialVersionUID = -9045063976646934342L;
	Vector<Schedule> schedules = new Vector<Schedule>();
	Vector<NamedDataGroup> namedDataGroup = new Vector<NamedDataGroup>();
	
	public void addNamedDataGroup(NamedDataGroup fPattern) {
		namedDataGroup.add(fPattern);
	}
	
	public FileSelection getFileSelection() {
		FileSelection fullSelection = new FileSelection();
		for(NamedDataGroup dGroup : namedDataGroup){
			fullSelection.addAll(dGroup.expandToFiles());
		}
		return fullSelection;
	}
	
	public boolean isScheduled(long time) {
		for(Schedule s : schedules){
			if(s.isScheduled(time)) return true;
		}
		return false;
	}
	
	public boolean willRunAgain() {
		for(Schedule s : schedules){
			if(s.willRunAgain()) return true;
		}
		return false;
	}
	
	public void addSchedule(Schedule schedule){
		if(schedule == null){
			throw new RuntimeException("Cannot add a null schedule to this job.");
		}
		this.schedules.add(schedule);
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getScheduleInformation());        
		buffer.append(getDataGroupInformation());
		return buffer.toString();
	}
	
	public String getScheduleInformation() {
		StringBuffer buffer = new StringBuffer();
		if(schedules.isEmpty()){
			buffer.append("<Never scheduled> : ");
		}
		else{
			for(Schedule s : schedules){
				buffer.append(s.getName());
				buffer.append(",");
			}
			
			//Remove the trailing ','
			buffer.deleteCharAt(buffer.length()-1);
			buffer.append(" : ");
		}
		return buffer.toString();
	}
	
	public String getDataGroupInformation(){
		StringBuffer buffer = new StringBuffer();
		if(namedDataGroup.isEmpty()){
			buffer.append("<No data group associated>");
		}
		else{
			for(NamedDataGroup fPattern : namedDataGroup){
				buffer.append(fPattern.getName());
				buffer.append(",");
			}
			//Remove the trailing ','
			buffer.deleteCharAt(buffer.length()-1);
		}
		return buffer.toString();
	}
	
	public NamedDataGroup getNamedDataGroup(int i) {
		return namedDataGroup.get(i);
	}

	public long getAffectedFilesSize() {
		FileSelection fselection = getFileSelection();
		return fselection.getTotalSize();
	}
}

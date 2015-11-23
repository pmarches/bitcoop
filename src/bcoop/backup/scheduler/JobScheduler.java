/**
 * <p>Title: JobScheduler.java</p>
 * <p>Description: A scheduler that can determine the next job to run</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import java.util.Vector;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import bcoop.backup.fileselection.NamedDataGroup;

/**
 * @author pmarches
 *
 */
public class JobScheduler {
    int lastJobNumber = 0;
    Hashtable<String, NamedDataGroup> definedDataGroup = new Hashtable<String, NamedDataGroup>();
    Hashtable<String, Schedule> definedSchedule = new Hashtable<String, Schedule>();
    
    Vector<ScheduledJob> definedJobs = new Vector<ScheduledJob>();
    LinkedList<ScheduledJob> jobsReadyToRun = new LinkedList<ScheduledJob>();

    public void addSchedule(Schedule newSchedule){
    		definedSchedule.put(newSchedule.getName(), newSchedule);
    }
    public Vector<Schedule> getAllSchedule(){
    		return new Vector<Schedule>(this.definedSchedule.values());
    }
    
    public void addJob(ScheduledJob newJob){
        Logger.getLogger(this.getClass()).debug("Defining job in JobScheduler "+newJob);
        definedJobs.add(newJob);
    }
    
    public ScheduledJob getNextScheduledJob(){
        if(jobsReadyToRun.isEmpty()) return null;
        return jobsReadyToRun.getFirst();
    }
    
    public void removeJob(ScheduledJob jobToRemove){
        jobsReadyToRun.remove(jobToRemove);
    }
    
    public void loadReadyJobs(long time){
        Logger.getLogger(this.getClass()).debug("Loading jobs that are ready to run from the "+definedJobs.size()+" jobs defined.");
        for(int i=0; i < definedJobs.size(); i++){
            ScheduledJob job = definedJobs.get(i);
            Logger.getLogger(this.getClass()).debug("Checking if Job "+job+ " is scheduled.");
            if(job.isScheduled(time)){
                Logger.getLogger(this.getClass()).debug("Job "+job+ " will never run again. Removing it from the definition.");
                if(!job.willRunAgain()) definedJobs.remove(job);
                jobsReadyToRun.addLast(job);
            }
        }
    }

    public Vector<ScheduledJob> getAllJobs() {
        return definedJobs;
    }

    public boolean hasJobReady() {
        return !jobsReadyToRun.isEmpty();
    }

    public void  addDataGroup(NamedDataGroup newGroup){
		definedDataGroup.put(newGroup.getName(), newGroup);
    }

	public Vector<NamedDataGroup> getNamedDataGroup() {
		return new Vector<NamedDataGroup>(this.definedDataGroup.values());
	}
	public NamedDataGroup getNamedDataGroup(String fileset) {
		return this.definedDataGroup.get(fileset);
	}
    
}

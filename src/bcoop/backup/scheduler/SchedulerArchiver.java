package bcoop.backup.scheduler;

import java.util.Vector;

import org.apache.log4j.Logger;

import bcoop.backup.fileselection.NamedDataGroup;
import bcoop.exception.MissingConfigurationException;
import bcoop.util.Configuration;

public class SchedulerArchiver {
	Configuration config;
	
	public SchedulerArchiver(Configuration config) {
		this.config = config;
	}

	public JobScheduler loadScheduler() throws MissingConfigurationException {
		JobScheduler jobScheduler = new JobScheduler();
		
		JobFactory jFactory = new JobFactory(config);
		
		Vector<ScheduledJob> jobs = jFactory.getJobs();
		Logger.getLogger(this.getClass()).debug("Found "+jobs.size()+ " jobs in the XML file.");
		for (ScheduledJob job : jobs) {
			Logger.getLogger(this.getClass()).debug("Loading job " + job);
			jobScheduler.addJob(job);
		}

		Vector<Schedule> schedules = jFactory.getSchedule();
		for (Schedule schedule : schedules) {
			Logger.getLogger(this.getClass()).debug("Loading schedule " + schedule.getName());
			jobScheduler.addSchedule(schedule);
		}

		Vector<NamedDataGroup> dataGroups = jFactory.getDataGroup();
		for (NamedDataGroup dataGroup : dataGroups) {
			Logger.getLogger(this.getClass()).debug("Loading datagroup " + dataGroup.getName());
			jobScheduler.addDataGroup(dataGroup);
		}
		return jobScheduler;
	}

}

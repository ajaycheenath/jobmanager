package com.af.job.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.af.job.core.Job;
import com.af.job.core.JobPriority;
/**
 * This method holds utilities for unit test
 * @author ajay_francis
 *
 */
@Component
public class JobTestUtils {
	
	@Autowired
	private ApplicationContext applicationContext;
	/**
	 * Method creates a new job object
	 * @param jobName
	 * @param jobPriority
	 * @param runAt
	 * @return
	 */
	public Job createJob(String jobName, JobPriority jobPriority, Long runAt) {
		Job job = applicationContext.getBean(Job.class);
		job.setJobName(jobName);
		job.setJobPriority(jobPriority);
		job.setCreatedTime(System.currentTimeMillis());
		if(runAt != null) {
			job.setRunAt(runAt);
		}
		return job;
	}
	
	/**
	 * This method generate job string for a job object
	 * @param jobName
	 * @param runAt
	 * @param priority
	 * @param configName
	 * @param configValue
	 * @return
	 */
	public String genJobAsString(String jobName, long runAt, String priority, String configName, String configValue) {
		String job =   "{\"jobName\": \""+jobName+"\",\"jobPriority\": \""+priority+"\",\"runAt\": "+runAt+",\"state\": \"QUEUED\",\"configs\": {\""+configName+"\": \""+configValue+"\"}}";
		return job;
	}
}

package com.af.job.core;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * POJO for holding job details
 * @author ajay_francis
 */
@Component
@Scope("prototype")
public class Job implements Serializable{

	private static final long serialVersionUID = 1L;
	private UUID jobId =  UUID.randomUUID();
	
	private Long createdTime;
	
	private String jobName;
	
	private JobPriority jobPriority;
	
	private Long runAt = 0L;
	
	private JobState state;
	
	/**
	 * Job Specific configurations. Will be used by Job Executor
	 */
	private Map<String, String> configs = new HashMap<String, String>();
	
	public Job() {
		
	}

	public Job(String jobName, JobPriority jobPriority) {
		this.jobName = jobName;
		this.jobPriority  = jobPriority;
	}
	
	public UUID getJobId() {
		return jobId;
	}
	public void setJobId(UUID jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public JobPriority getJobPriority() {
		return jobPriority;
	}

	public int getJobPriorityValue() {
		return jobPriority.getValue();
	}
	
	public void setJobPriority(JobPriority jobPriority) {
		this.jobPriority = jobPriority;
	}

	public Long getRunAt() {
		return this.runAt;
	}

	public void setRunAt(Long runAt) {
		this.runAt = runAt;
	}

	public JobState getState() {
		return state;
	}
	
	public void setState(JobState state) {
		this.state = state;
	}

	public Map<String, String> getConfigs() {
		return configs;
	}
	public void setConfigs(Map<String, String> configs) {
		this.configs = configs;
	}
	public String getConfig(String configName) {
		return configs.get(configName);
	}
	
	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//TODO: use string constant
		sb.append(jobId).append(",").append(jobName).append(",").append(jobPriority).append(",").append(runAt).append(",").append(createdTime);
		return sb.toString();
	}
}

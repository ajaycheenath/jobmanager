package com.af.job.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.af.job.core.Job;


/**
 * This class is a In-memory storage for DB access.
 * TODO: This class need to be replaced with JobDAO and JobDAOImpl to persist job object
 * Persisting Job in Disk DB is important to handle application restart / recovery and to control the Job Queue elements
 * @author ajay_francis
 *
 */
@Service
public class JobDAOImpl implements JobDAO{

	private Map<UUID, Job> jobsInMemoryStore = new HashMap<UUID, Job>();
	
	/**
	 * Method to save Job object
	 * @param job
	 */
	@Override
	public void save(Job job) {
		jobsInMemoryStore.put(job.getJobId(), job);

	}
	
	/**
	 * Method will return Job object for the jobId passed
	 * @param jobId
	 * @return
	 */
	@Override
	public Job getJob(UUID jobId) {
		return jobsInMemoryStore.get(jobId);
	}

	/**
	 * This method return list of all jobs
	 * @return
	 */
	@Override
	public List<Job> getAllJobs() {
		List<Job> jobs = new ArrayList<Job>(jobsInMemoryStore.values());
		Collections.sort(jobs, Comparator.comparing(Job::getRunAt)
				.thenComparing(Job::getJobPriorityValue)
					.thenComparing(Job::getCreatedTime));
		return jobs;
	}

}

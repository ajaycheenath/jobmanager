package com.af.job.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.af.job.core.Job;

/**
 * This class is a mock for DB access.
 * TODO: This class need to be replaced with JobDAO and JobDAOImpl to persist job object
 * @author ajay_francis
 *
 */
@Service
public interface JobDAO {
	
	/**
	 * Method to save Job object
	 * @param job
	 */
	public void save(Job job);
	
	/**
	 * Method will return Job object for the jobId passed
	 * @param jobId
	 * @return
	 */
	public Job getJob(UUID jobId);
	
	/**
	 * This method return list of all jobs
	 * @return
	 */
	public List<Job> getAllJobs();
	

}

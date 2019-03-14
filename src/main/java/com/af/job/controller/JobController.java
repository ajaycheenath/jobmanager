package com.af.job.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.af.job.core.Job;
import com.af.job.core.JobExecutor;
import com.af.job.core.JobManager;
import com.af.job.dao.JobDAO;

/**
 * This class act as REST end point for JobManager application
 * The job of this class is to provide access to operations on JobManager application such as Add a new Job, Get details about a job or get list of all jobs
 * @author ajay_francis
 *
 */
@RestController
@RequestMapping(value="/job")	
public class JobController {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobDAO dao;

	private Logger logger = LoggerFactory.getLogger(JobController.class);
	
	/**
	 * This method provide list of jobs and job details(current status) 
	 * If jobId is passed as path variable, a specific job will be returned else all jobs will be returned as a list
	 * TODO: Return entire job list may cause performance issue. Require limit jobs by pagination (from - to) to handle performance
	 * @param jobId 
	 * @return json - list of jobs
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value= {"", "/{jobId}"})
	public @ResponseBody ResponseEntity<List<Job>> addJob(@PathVariable(value="jobId", required=false) UUID jobId) throws Exception {
		logger.info("/GET /job/ - {}", jobId);
		List<Job> jobs = dao.getAllJobs();
		return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
	}
	/**
	 * This method allows to add a new job for execution
	 * @param jobType
	 * @param job:{ jobName : mandatory, jobPriority: HIGH/MEDIUM/LOW, runAt: in milliseconds }
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, value="/add/{jobType}")
	public @ResponseBody ResponseEntity<String> addJob(@PathVariable(value="jobType") String jobType, @RequestBody Job job) throws Exception {
		logger.info("/POST /job/add/{}", jobType);
		JobExecutor jobExecutor = null;
		job.setCreatedTime(System.currentTimeMillis());
		try {
			jobExecutor = (JobExecutor) applicationContext.getBean(jobType);
		}catch(Exception e) {
			return new ResponseEntity<String>("ERROR: Job Type : '"+jobType+"' is not valid", HttpStatus.BAD_REQUEST);//TODO: use string constant
		}
		try {
			jobManager.addJob(job, jobExecutor);
		}
		catch(Exception e) {
			logger.error("/add/{jobType} ", e);
			return new ResponseEntity<String>("ERROR: "+e.getMessage(), HttpStatus.BAD_REQUEST);//TODO: use string constant
		}	
			return new ResponseEntity<String>("Successfully added Job id: "+job.getJobId(), HttpStatus.OK);//TODO: use string constant
		}
}
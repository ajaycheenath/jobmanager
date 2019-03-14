package com.af.job.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.af.job.dao.JobDAO;

/**
 * This class handles job execution. JobManager framework doesn't really know the how to execute a job/task so made this class as abstract.
 * The child class of this abstract class has to implement execute and validate method to let the framework know how to validate job configurations and execute the job
 * @author ajay_francis
 *
 */
@Component
public abstract class JobExecutor implements Runnable{
	
	
	private Job job;
	private JobStateChangeListener stateChangeListener;
	
	@Autowired
	private JobDAO dao;
	private Logger logger = LoggerFactory.getLogger(JobExecutor.class);
	
	/**
	 * Validate is an abstract method which will have to be implemented by the child concrete class 
	 * When a new job gets added, validate method of the jobexecutor implementation gets called to validate the configuration
	 * @param job
	 * @throws Exception
	 */
	public abstract void validate(Job job) throws Exception;
	
	/**
	 * Implement logic to execute a job.
	 * Throwing exception will be considered as failure.
	 * @return JobState (SUCESS / FAILED)
	 */
	public abstract JobState execute(Job job);

	/**
	 * Jobexecutor is one runnable instance. 
	 * JobManager execute a Job when it turn comes. 
	 * This method mark a Job in RUNNING state just before the execution and SUCCESS / FAILURE after the run.
	 */
	@Override
	public void run() {
		logger.info("JobExecutor.run() Job: {}", job);
		try {
			this.setState(JobState.RUNNING);
			dao.save(job);
			JobState state = execute(job);
			this.setState(state);
			dao.save(job);
		}catch(Exception e) {//Any exception thrown by Job execute implementation should mark Job state as Failed and there should be no side-effects
			logger.error("JobExecutor.run() ", e);
			this.setState(JobState.FAILED);
			dao.save(job);
		}
	}
	/**
	 * This method allow to set the state of a job
	 * When state get changed, stateChangeListener callback will triggered
	 * @param state
	 */
	protected void setState(JobState state) {
		logger.debug("/setState: state = {}, job = {}", state, job);
		//Make sure State Change Listener called when there is state change
		if(this.job.getState() != state && this.getStateChangeListener() != null) {
			this.stateChangeListener.stateChange(this.job.getJobId(), this.job.getJobName(), state);
		}
		this.job.setState(state);
		dao.save(job);
	}
	
	public JobStateChangeListener getStateChangeListener() {
		return stateChangeListener;
	}
	/**
	 * JobStateChangeListener get triggered when there is a state change in job state.
	 * @param stateChangeListener
	 */
	public void setStateChangeListener(JobStateChangeListener stateChangeListener) {
		this.stateChangeListener = stateChangeListener;
	}
	
	public Job getJob() {
		return job;
	}
	
	public void setJob(Job job) {
		this.job = job;
	}
}

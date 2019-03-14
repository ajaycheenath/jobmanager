package com.af.job.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.af.job.core.Job;
import com.af.job.core.JobExecutor;
import com.af.job.core.JobState;
import com.af.job.utils.CommonUtils;

/**
 * An example implementation of JobExecutor
 * This class helps check a website url is reachable by doing a GET method. 
 * The url to check and the timeout while sending a HTTP call is passed as configuration(Map)
 * @author ajay_francis
 *
 */
@Service("reachability")
@Scope("prototype")
public class ReachabilityCheck extends JobExecutor {
	
	public static final String URL = "url";
	public static final String TIME_OUT = "timeOut";
	
	@Value("${http.default.timeout:60000}")
	private int DEFAULT_TIME_OUT;
	
	@Autowired
	private CommonUtils utils;
	
	private Logger logger = LoggerFactory.getLogger(ReachabilityCheck.class);

	/**
	 * This method gets triggered by JobManager when its turn comes.
	 * The job of execute method is to do the actual job/task, in this case it is sending HTTP GET command to the URL specified in the configuration and report the result
	 * Upon completion Success / Failure , a job implementation needs return the JobState
	 */
	@Override
	public JobState execute(Job job) {
		JobState state = null;
		String url = job.getConfig(URL);
		String timeout = job.getConfig(TIME_OUT);
		int timeoutValue = (timeout == null) ? DEFAULT_TIME_OUT : Integer.parseInt(timeout);
		try {
			utils.sendGet(url, timeoutValue);
			state = JobState.SUCCESS;
		} catch (Exception e) {//TODO: print error in log
			e.printStackTrace();
			state = JobState.FAILED;
		}
		logger.debug("Done with job {}", job);
		return state;
	}

	/**
	 * When a new job gets added, validate method of the jobexecutor gets called to validate the configuration 
	 */
	@Override
	public void validate(Job job) throws Exception {
		if(job.getConfig(URL) == null) {
			throw new Exception("Mandatory configuration "+URL+" not found!");
		}
	}

}

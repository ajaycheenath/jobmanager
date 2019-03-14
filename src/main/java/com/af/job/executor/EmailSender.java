package com.af.job.executor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.af.job.core.Job;
import com.af.job.core.JobExecutor;
import com.af.job.core.JobState;
import com.af.job.utils.CommonUtils;

/**
 * An example implementation of JobExecutor
 * This class helps to send email. 
 * In order to send email, toAddress, fromAddress, subject, smtp server details and content need to be passed as configuration
 * @author ajay_francis
 *
 */
@Service("emailsender")
@Scope("prototype")
public class EmailSender extends JobExecutor {
	
	public static final String TO_ADDRESS = "toAddress";

	@Autowired
	private CommonUtils utils;
	

	/**
	 * This method gets triggered by JobManager when its turn comes.
	 * This method takes care of sending email to the address specified.
	 * Configurations are retrieved using job.getConfig() 
	 * Upon completion Success / Failure , a job implementation needs return the JobState
	 */
	@Override
	public JobState execute(Job job) {
		JobState state = null;
		Map<String,String> configs = job.getConfigs();//Configs required for sending email
		try {
			utils.sendEmail(configs);
			state = JobState.SUCCESS;
		} catch (Exception e) {//TODO: print error in log
			e.printStackTrace();
			state = JobState.FAILED;
		}
		return state;
	}
	
	/**
	 *When a new job gets added, validate method of the jobexecutor gets called to validate the configuration 
	 */
	@Override
	public void validate(Job job) throws Exception{
		if(job.getConfig(TO_ADDRESS) == null) {
			throw new Exception("Mandatory configuration "+TO_ADDRESS+ " not found");
		}
	}

}

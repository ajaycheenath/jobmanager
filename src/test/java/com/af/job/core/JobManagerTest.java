package com.af.job.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.af.job.executor.ReachabilityCheck;
import com.af.job.utils.CommonUtils;
import com.af.job.utils.JobTestUtils;

/**
 * This Test class validates JobManager functionalities
 * @author ajay_francis
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JobManagerTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JobManager jobManager;

	@Autowired
	private JobTestUtils jobUtils;

	@MockBean
	private CommonUtils utils;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobManagerTest.class);

	/**
	 * verify the application context is set properly
	 */
	@Test
	public void verifyContextInitiated() {
		assertNotNull(applicationContext);
	}

	/**
	 * Helper metod to create a new Job Object with StateChange Listerner object 
	 * @param status
	 * @param jobs
	 * @param job
	 * @return
	 */
	public JobExecutor createJobExecutor(JobState status, List<Job> jobs, Job job) {
		JobExecutor jobExecutor = (JobExecutor) applicationContext.getBean("reachability");
		Map<String, String> configs = new HashMap<String, String>();
		configs.put(ReachabilityCheck.URL, "https://google.com");
		job.setConfigs(configs);

		jobExecutor.setStateChangeListener(new JobStateChangeListener() {

			@Override
			public void stateChange(UUID jobId, String jobName, JobState event) {
				if(event == JobState.SUCCESS) {
					synchronized (job) {
						LOGGER.debug("---- Done with : {}", job);
						jobs.add(job);
					}
				}
			}
		});
		return jobExecutor;
	}

	/**
	 * Verify added jobs are sorted in the order (Schedule to run at, priority, creation time)
	 * @throws Exception
	 */
	@Test
	public void verifyJobsSortedByPriorityRunAtAndCreatedTime() throws Exception {
		Mockito.doNothing().when(utils).sendGet(null, 0);
		List<Job> jobs = new ArrayList<Job>();

		Long currentTime = System.currentTimeMillis();

		Job job1 = jobUtils.createJob("job1", JobPriority.LOW, currentTime+5000);
		jobManager.addJob(job1, createJobExecutor(JobState.SUCCESS, jobs, job1));

		Job job2 = jobUtils.createJob("job2", JobPriority.MEDIUM, currentTime+5000);
		jobManager.addJob(job2, createJobExecutor(JobState.SUCCESS, jobs, job2));

		Job job3 = jobUtils.createJob("job3", JobPriority.HIGH, currentTime+5000);
		jobManager.addJob(job3, createJobExecutor(JobState.SUCCESS, jobs, job3));
		
		Job job4 = jobUtils.createJob("job4", JobPriority.LOW, currentTime+1000L);
		jobManager.addJob(job4, createJobExecutor(JobState.SUCCESS, jobs, job4));
		
		Job job5 = jobUtils.createJob("job5", JobPriority.HIGH, currentTime+1000L);
		jobManager.addJob(job5, createJobExecutor(JobState.SUCCESS, jobs, job5));
		//Can put a  completed job counter and wait notify to have little more predictable result, than polling for state change
		Thread.sleep(5000);
		while(true) {
			if(jobs.size() == 5) {
				for(Job job : jobs) {
					LOGGER.info("Completed job : {}", job);
				}
				break;
			}
			Thread.sleep(1000);
			LOGGER.debug("No of completed jobs : {}", jobs.size());
		}
		
		assertEquals(jobs.get(0).getJobName(), "job5");
		assertEquals(jobs.get(1).getJobName(), "job4");
		assertEquals(jobs.get(2).getJobName(), "job3");
		assertEquals(jobs.get(3).getJobName(), "job2");
		assertEquals(jobs.get(4).getJobName(), "job1");

	}
}

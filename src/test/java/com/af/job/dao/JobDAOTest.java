package com.af.job.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.af.job.core.Job;
import com.af.job.core.JobPriority;
import com.af.job.utils.JobTestUtils;

/**
 * Unit Test calss to verify JobDAO persistent class functionalities
 * @author ajay_francis
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JobDAOTest {
	
	@Autowired
	private ApplicationContext applicationContext;


	@Autowired
	private JobDAO dao;
	
	@Autowired
	private JobTestUtils jobUtils;
	
	/**
	 * Method to verify application context set properly
	 */
	@Test
	public void verifyContextInitiated() {
		assertNotNull(applicationContext);
	}
	
	/**
	 * Method to verify jobs added in the store are sorted in the " priority, creation time" order
	 * @throws Exception
	 */
	@Test
	public void verifyJobsSortedByPriorityAndCreatedTime() throws Exception {
		Job job1 = jobUtils.createJob("job1", JobPriority.LOW, null);//6
		Job job2 = jobUtils.createJob("job2", JobPriority.MEDIUM, null);//4
		Job job3 = jobUtils.createJob("job3", JobPriority.HIGH, null);//1
		Thread.sleep(100);//Delay to have different createdTime
		Job job4 = jobUtils.createJob("job4", JobPriority.HIGH, null);//2
		Job job5 = jobUtils.createJob("job5", JobPriority.LOW, null);//7
		Job job6 = jobUtils.createJob("job6", JobPriority.MEDIUM, null);//5
		Thread.sleep(100);
		Job job7 = jobUtils.createJob("job7", JobPriority.HIGH, null);//3
		
		dao.save(job1);
		dao.save(job2);
		dao.save(job3);
		dao.save(job4);
		dao.save(job5);
		dao.save(job6);
		dao.save(job7);
		
		 List<Job> jobs = dao.getAllJobs();
		 assertEquals(jobs.get(0).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(0).getJobName().equals("job3"), true);
		 
		 assertEquals(jobs.get(1).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(1).getJobName().equals("job4"), true);
		 assertEquals(jobs.get(2).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(3).getJobPriority() == JobPriority.MEDIUM, true);
		 assertEquals(jobs.get(4).getJobPriority() == JobPriority.MEDIUM, true);
		 assertEquals(jobs.get(5).getJobPriority() == JobPriority.LOW, true);
		 assertEquals(jobs.get(5).getJobPriority() == JobPriority.LOW, true);
		
	}
	/**
	 * Method to verify jobs added in the store are sorted in the " Scheduled to run at, priority, creation time" order
	 * @throws Exception
	 */
	@Test
	public void verifyJobsSortedByPriorityRunAtCreatedTimeOnlyHighPriority() throws Exception {
		Long currentTime = System.nanoTime();
		Job job1 = jobUtils.createJob("job1", JobPriority.LOW, null);//6
		Job job2 = jobUtils.createJob("job2", JobPriority.MEDIUM, null);//4
		Job job3 = jobUtils.createJob("job3", JobPriority.HIGH, (currentTime * 3));//1
		Job job4 = jobUtils.createJob("job4", JobPriority.HIGH, (currentTime * 2));//2
		Job job5 = jobUtils.createJob("job5", JobPriority.LOW, null);//7
		Job job6 = jobUtils.createJob("job6", JobPriority.MEDIUM, null);//5
		Job job7 = jobUtils.createJob("job7", JobPriority.HIGH, null);//3
		
		dao.save(job1);
		dao.save(job2);
		dao.save(job3);
		dao.save(job4);
		dao.save(job5);
		dao.save(job6);
		dao.save(job7);
		
		 List<Job> jobs = dao.getAllJobs();
		 assertEquals(jobs.get(0).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(0).getJobName().equals("job7"), true);
		 
		 assertEquals(jobs.get(1).getJobPriority() == JobPriority.MEDIUM, true);
		 assertEquals(jobs.get(2).getJobPriority() == JobPriority.MEDIUM, true);
		 
		 assertEquals(jobs.get(3).getJobPriority() == JobPriority.LOW, true);
		 assertEquals(jobs.get(4).getJobPriority() == JobPriority.LOW, true);
		 
		 assertEquals(jobs.get(5).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(5).getJobName().equals("job4"), true);
		 
		 assertEquals(jobs.get(6).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(6).getJobName().equals("job3"), true);
		
	}
	/**
	 * Method to verify jobs added in the store are sorted in the " priority, creation time" order with more combinations
	 * @throws Exception
	 */
	@Test
	public void verifyJobsSortedByPriorityRunAtCreatedTime() throws Exception {
		Long currentTime = System.currentTimeMillis();
		Job job1 = jobUtils.createJob("job1", JobPriority.LOW, (currentTime * 2));//6
		Job job2 = jobUtils.createJob("job2", JobPriority.MEDIUM, null);//2
		Job job3 = jobUtils.createJob("job3", JobPriority.HIGH, (currentTime * 3));//7
		Job job4 = jobUtils.createJob("job4", JobPriority.HIGH, (currentTime * 2));//4
		Job job5 = jobUtils.createJob("job5", JobPriority.LOW, null);//3
		Job job6 = jobUtils.createJob("job6", JobPriority.MEDIUM, currentTime * 2);//5
		Job job7 = jobUtils.createJob("job7", JobPriority.HIGH, null);//1
		Job job8 = jobUtils.createJob("job8", JobPriority.HIGH, (currentTime * 3));//8
		
		dao.save(job1);
		dao.save(job2);
		dao.save(job3);
		dao.save(job4);
		dao.save(job5);
		dao.save(job6);
		dao.save(job7);
		dao.save(job8);
		
		 List<Job> jobs = dao.getAllJobs();
		 assertEquals(jobs.get(0).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(0).getJobName().equals("job7"), true);
		 
		 assertEquals(jobs.get(1).getJobPriority() == JobPriority.MEDIUM, true);
		 assertEquals(jobs.get(1).getJobName().equals("job2"), true);
		 
		 assertEquals(jobs.get(2).getJobPriority() == JobPriority.LOW, true);
		 assertEquals(jobs.get(2).getJobName().equals("job5"), true);
		 
		 assertEquals(jobs.get(3).getJobPriority() == JobPriority.HIGH, true);
		 assertEquals(jobs.get(3).getJobName().equals("job4"), true);
		 
		 assertEquals(jobs.get(4).getJobPriority() == JobPriority.MEDIUM, true);
		 assertEquals(jobs.get(4).getJobName().equals("job6"), true);
		 
		 assertEquals(jobs.get(5).getJobPriority() == JobPriority.LOW, true);
		 assertEquals(jobs.get(5).getJobName().equals("job1"), true);
		 
		 //Depending upon creationTime job 3 or 8 can come in 6th position
		 assertEquals(jobs.get(6).getJobPriority() == JobPriority.HIGH, true);
		 
		 assertEquals(jobs.get(7).getJobPriority() == JobPriority.HIGH, true);
		
		
	}
}

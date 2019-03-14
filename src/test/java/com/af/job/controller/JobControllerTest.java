package com.af.job.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.af.job.utils.CommonUtils;
import com.af.job.utils.JobTestUtils;


/**
 * This class verify units of JobController class
 * @author ajay_francis
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class JobControllerTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JobTestUtils jobUtils;

	@MockBean
	private CommonUtils utils;
	
	/**
	 * verify whether the application context is set
	 */
	@Test
	public void verifyContextInitiated_Test() {
		assertNotNull(applicationContext);
	}

	/**
	 * verify whether a job can be added with proper job type
	 * @throws Exception
	 */
	@Test
	public void addJobNoJobTypeTest() throws Exception {
		String job1 =   jobUtils.genJobAsString("job1", 0, "HIGH", "url", "https://google.com");
		mockMvc.perform(post("/job/add/")
				.contentType(MediaType.APPLICATION_JSON).content(job1))
		.andExpect(status().isMethodNotAllowed());
	}
	/**
	 * Verify add operation fail gracefully is a wrong job type is passed
	 * @throws Exception
	 */
	@Test
	public void addJobWrongJobTypeTest() throws Exception {
		String job1 =   jobUtils.genJobAsString("job1", 0, "HIGH", "url", "https://google.com");
		mockMvc.perform(post("/job/add/wrongjobtype")
				.contentType(MediaType.APPLICATION_JSON).content(job1))
		.andExpect(status().isBadRequest());
	}
	/*
	 * Verify after adding a new job, same can be retrieved using GET /job REST end point
	 */
	@Test
	public void addJobTest() throws Exception {
		String job1 =  jobUtils.genJobAsString("job1", 0, "HIGH", "url", "https://google.com");
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job1)).andDo(print())
		.andExpect(status().isOk());

		//Verify the Job is available in the in-memory store
		mockMvc.perform(get("/job")).andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(1)));
	}
	/**
	 * Verify jobs added job manager sort them in the right order (Scheduled time, Priority, creation time)
	 * @throws Exception
	 */
	@Test
	public void addjobsandverifyorder() throws Exception {
		Mockito.doNothing().when(utils).sendGet(null, 0);
		long runtAt = System.currentTimeMillis() + 60000;//Run it later
		//6
		String job1 = jobUtils.genJobAsString("job1", runtAt, "LOW", "url", "https://google.com");
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job1)).andDo(print())
		.andExpect(status().isOk());
		//5
		String job2 = jobUtils.genJobAsString("job2", runtAt, "MEDIUM", "url", "https://google.com"); 
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job2)).andDo(print())
		.andExpect(status().isOk());

		//3
		String job3 =   jobUtils.genJobAsString("job3", runtAt, "HIGH", "url", "https://google.com");
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job3)).andDo(print())
		.andExpect(status().isOk());

		Thread.sleep(100);
		//4
		String job4 =   jobUtils.genJobAsString("job4", runtAt, "HIGH", "url", "https://google.com");
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job4)).andDo(print())
		.andExpect(status().isOk());

		//1
		String job5 =  jobUtils.genJobAsString("job5", 0, "HIGH", "url", "https://google.com");
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job5)).andDo(print())
		.andExpect(status().isOk());

		//2
		String job6 =   jobUtils.genJobAsString("job6", 0, "MEDIUM", "url", "https://google.com");
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job6)).andDo(print())
		.andExpect(status().isOk());

		//Verify the Job is available in the inmeory store
		mockMvc.perform(get("/job")).andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.[0].jobName").value("job5")).andExpect(jsonPath("$.[1].jobName").value("job6")).andExpect(jsonPath("$.[2].jobName").value("job3"))
		.andExpect(jsonPath("$.[3].jobName").value("job4")).andExpect(jsonPath("$.[4].jobName").value("job2")).andExpect(jsonPath("$.[5].jobName").value("job1"));
	}
	/**
	 * makes sure add job gracefully fail if we don't/valid passed required configuration
	 * @throws Exception
	 */
	@Test
	public void addJobConfigValidationTest() throws Exception {
		String job1 =   "{\"jobName\": \"job1\",\"jobPriority\": \"LOW\",\"runAt\": 0,\"state\": \"QUEUED\",\"configs\": {}}";
		mockMvc.perform(post("/job/add/reachability")
				.contentType(MediaType.APPLICATION_JSON).content(job1)).andDo(print())
		.andExpect(status().isBadRequest());

	}
}

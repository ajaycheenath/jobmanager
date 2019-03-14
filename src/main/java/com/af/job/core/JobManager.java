package com.af.job.core;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.af.job.dao.JobDAO;

/**
 * This class is the heart of JobManager application
 * It allows to add a new manage its order of execution and trigger execution
 * @author ajay_francis
 *
 */
@Service
public class JobManager {
	private ExecutorService priorityJobPoolExecutor;
	private ExecutorService executor; 
	//TODO: Having more number of threads to process can improve performance, when changing consider changing unit test job verification order
	private PriorityBlockingQueue<JobExecutor> priorityJobQueue;
	private Logger logger = LoggerFactory.getLogger(JobManager.class);
	

	@Value("${job.executor.pool.size}")
	private int executorPoolSize;

	@Value("${job.priority.queue.size}")
	private int queueSize;

	@Autowired
	private JobDAO dao;

	/**
	 * This method initialize the jobmanager.
	 * The main part of the jobmanager is PriorityBlockingQueue.
	 * A new job will be first added to the  PriorityBlockingQueue, each time a new job is inserted, jobs in the Q will be sorted per "scheduled time", "priority" and "creation time"
	 * This make the queue to maintain the execution order of each job
	 */
	@PostConstruct
	public void init() {
		priorityJobPoolExecutor = Executors.newFixedThreadPool(executorPoolSize);
		executor = Executors.newSingleThreadExecutor();
		priorityJobQueue = new PriorityBlockingQueue<JobExecutor>(queueSize, 
				Comparator.comparing(JobExecutor::getJob, Comparator.comparing(Job::getRunAt))
				.thenComparing(JobExecutor::getJob, Comparator.comparing(Job::getJobPriorityValue))
				.thenComparing(JobExecutor::getJob, Comparator.comparing(Job::getCreatedTime)));
		logger.info("Job Manager Service intialized with poolSize = {} and queueSize = {}", executorPoolSize, queueSize);
		executor.execute(()->{
			while (true) {
				try {
					JobExecutor jobExecutor = priorityJobQueue.peek();
					if(jobExecutor == null) {
						synchronized (this) {
							logger.debug("Waiting for new Job");
							wait();
							jobExecutor = priorityJobQueue.peek();//Handle new Job added into the Queue
							if(jobExecutor == null) {
								logger.debug("******* JOB = "+jobExecutor);
								continue;
							}
						}
					}

					//Find when to execute next(head) job ?
					Job job = jobExecutor.getJob();
					long waitTime = job.getRunAt() - System.currentTimeMillis();
					logger.debug("waitTime = "+waitTime+", "+job.getRunAt() +" = "+System.currentTimeMillis());
					if(waitTime > 0) {
						synchronized (this) {
							logger.debug("Job "+job.getJobName()+" will be execute after "+waitTime+"ms");
							wait(waitTime);
						}
					}else {
						priorityJobPoolExecutor.execute(priorityJobQueue.take());
					}
				} catch (InterruptedException e) {
					logger.debug("Wait interrupted ", e);
					break;
				}
			}
		});
	}
	/**
	 * This method add a new Job into the priorityQueue and that get eventually executed when the Criteria met
	 * The solution expect the processing rate is faster than the new job adding rate.
	 * If that is not the case, we will have to first store job in DB and then periodically added fixed number of jobs into the priorityQueue to process
	 * @param job
	 * @param jobExecutor
	 * @return
	 * @throws Exception
	 */
	public synchronized Job addJob(Job job, JobExecutor jobExecutor) throws Exception {
		logger.info("/addJob - Job ID = {}", job);
		jobExecutor.validate(job);
		jobExecutor.setJob(job);
		priorityJobQueue.add(jobExecutor);
		jobExecutor.setState(JobState.QUEUED);
		notify();
		dao.save(job);
		return job;
	}
	
	/**
	 * This method return the current number of jobs in the Queue
	 * @return
	 */
	public int getJobCount() {
		return priorityJobQueue.size();
	}
}
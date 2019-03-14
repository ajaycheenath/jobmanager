##### Job Manager - version 0.1
Job Managemer is a simple Service to manage
execution of multiple types of Jobs. The actions performed by these Jobs can be anything.

Examples of these Jobs could be performing a URL reachability check or sending emails.

Features supported by Job Manager

###### Flexibility

- The action performed by the Jobs can be anything. Adding a new Job Type require just implementing two methods.

###### Reliability

- The status of any job execution would not have any impact on other Jobs. That means, even if a job failed during execution, that would not impact other jobs executing or yet to execute.

###### Internal Consistency
- At any one time a Job has one of four states: QUEUED, RUNNING, SUCCESS, FAILED. Following
the execution of a Job, a job will always be left in an appropriate state.

###### Priority
- Each Job can be executed based on its priority relative to other Jobs. Priorities supported by Job Manager are HIGH, MEDIUM and LOW

###### Scheduling

- A Job can be executed immediately or according to a schedule.

Java Version
> 1.8 or above

##### How to test ?
run > mvn test

##### How to build ?
run > mvn install

##### How to run ?

cd target

run> java -jar JobManager-0.0.1-SNAPSHOT.jar

#### Design

JavaManager is one spring-boot application written in java. The heart of the application is JavaManager.java, one spring service which has a Sorted Priority Queue to store and maintain jobs in sorted order (Sorted by Scheduled Time, Priority and Creation Time). This ensures the next job pop from the head is always the next job to be executed. Jobs dequeued from the priority queue are then added to a ThreadPool(ExecutorService) to execute.


#### How to add a new Job Type?
-  In order to create a new Job Type / Excecutor, you will have to extent JobExecutor class and implement 'execute' and 'validate' methods as below. Yes it is as simple as that!


```
@Service("emailsender")
@Scope("prototype")
public class EmailSender extends JobExecutor {
  @Override
	public JobState execute(Job job) {
      //Logic to perform job/action
      return JobState.SUCCESS
  }

  @Override
	public void validate(Job job) throws Exception{
      //Add logic to validate configuration required to execute job
		 if(job.getConfig(TO_ADDRESS) == null) {
			       throw new Exception("Mandatory configuration "+TO_ADDRESS+ " not found");
         }
	}

}

```

#### How to add a new Job of type "emailsender"?
When JobExecutor is in place, adding a new job is very easy. This can be done just by invoking JobManager generic REST endpoint as below

```
POST http://localhost:8080/job/add/emailsender

{
	"jobName": "GooleURLCheck",
	"jobPriority": "MEDIUM",
	"runAt": "1562027496450",
	"configs" : {
		"tp" : "ajaycheenath@xyz.com"
	}
}

response :

Successfully added Job id: e2b58be0-6e76-4b5b-92a0-4f9ece700a93

```

#### How to fetch all Jobs?
```
GET http://localhost:8080/job

response :

[
    {
        "jobId": "c7ba4763-da3d-4659-9ed1-7aa42fee8101",
        "createdTime": 1552570341619,
        "jobName": "google5.com",
        "jobPriority": "MEDIUM",
        "runAt": 0,
        "state": "SUCCESS",
        "configs": {
            "url": "https://google.com"
        },
        "jobPriorityValue": 2
    },
    {
        "jobId": "317e3e19-811e-4b8a-8beb-f2236bb06741",
        "createdTime": 1552570379810,
        "jobName": "google5.com",
        "jobPriority": "MEDIUM",
        "runAt": 0,
        "state": "SUCCESS",
        "configs": {
            "url": "https://google.com"
        },
        "jobPriorityValue": 2
    },
    ....
```

##### Limitations

- JobManager store jobs in in-memory Priory Queue. When application go for a restart, jobs in the queue will be lost.

- If the rate at which jobs gets added are more than the rate at which jobs gets executed, there is chance the queue may exceed the memory limit.

- GET /jobs fetch all jobs. Which can be a performance hit when there are many jobs. We can solve this by adding record limit (say only 10 records)

##### Enhancements

- Persisting jobs first and then pushing only limited/controlled sorted set of jobs for processing can solve the above two limitations. But the cost for that solution would be the delay in processing jobs.

- In the interest of time, JobDAOImpl storing data in a Map. This needs to be enhanced to store in DB using SpringData/ORM

- In order to add a new Job Type/Job Executor, one will have to add code and restart the application. If the requirement is to dynamically add a new jobtype, we can introduce a REST end point to upload jar file with new code. We would require a custom class loader to load the new JobType. Also in order to avoid class conflicts, we would need separate class loaders per job type jar file.

- GET /job - Filter by Job Type
- GET /job - Filter by JobState

- Unit Testing: Current code does more of functional tests rather than unit. It require more use case coverage and mocking of objects.

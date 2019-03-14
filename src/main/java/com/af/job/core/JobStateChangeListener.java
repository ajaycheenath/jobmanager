package com.af.job.core;

import java.util.UUID;

/**
 * An interface used as callback when there is change in Job State
 * @author ajay_francis
 *
 */
public interface JobStateChangeListener {
	public void stateChange(UUID jobId, String jobName, JobState event);
}

package org.ajp.server.event;

import org.ajp.server.model.JobRequest;

public class JobEvent {

	private JobRequest jobRequest;

	public JobEvent(JobRequest jobRequest) {
		this.setJobRequest(jobRequest);
	}

	public JobRequest getJobRequest() {
		return jobRequest;
	}

	public void setJobRequest(JobRequest jobRequest) {
		this.jobRequest = jobRequest;
	}

}

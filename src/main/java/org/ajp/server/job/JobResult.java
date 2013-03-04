package org.ajp.server.job;

public class JobResult {
	public enum JobResultStatus {
		SUCCESS, FAILURE
	}

	private JobResultStatus status;

	public JobResultStatus getStatus() {
		return status;
	}

	public void setStatus(JobResultStatus status) {
		this.status = status;
	}
}

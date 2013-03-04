package org.ajp.server.job;

import java.util.concurrent.Callable;

import org.ajp.server.job.JobResult.JobResultStatus;
import org.ajp.server.model.JobRequest;


public class Job implements Callable<JobResult> {

	private JobRequest jobRequest = null;

	public Job(final JobRequest jreq) {
		this.jobRequest = jreq;
	}

	@Override
	public JobResult call() throws Exception {
		JobResult jres = new JobResult();
		
		jobRequest.getId();
		
		jres.setStatus(JobResultStatus.SUCCESS);
		
		return jres;
	}
}

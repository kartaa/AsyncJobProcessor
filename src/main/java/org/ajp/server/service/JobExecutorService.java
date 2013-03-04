package org.ajp.server.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.ajp.server.event.JobEvent;
import org.ajp.server.job.Job;
import org.ajp.server.job.JobResult;
import org.ajp.server.job.JobResult.JobResultStatus;
import org.ajp.server.model.JobRequest;
import org.ajp.server.util.AJPServerProperties;

@Stateless
@LocalBean
public class JobExecutorService {

	@Inject
	private Logger log;

	@Inject
	private JobLifeCycleService jobLifeCycle;

	private ThreadPoolExecutor tpExecutor;

	// Some constants for the Thread Pool Executor

	private int corePoolSize = 5;
	private int maxPoolSize = Integer.parseInt(AJPServerProperties
			.getInstance().getProperty("executor.maxpoolsize"));
	private long keepAliveTime = 10000;
	private int workQueueSize = 50;

	@Asynchronous
	public void processNewJob(@Observes JobEvent jobEvent) {
		// Process a new Job as it is created
		JobRequest j = jobEvent.getJobRequest();
		jobLifeCycle.setAsWaiting(j.getId());

		// Create a new Callable Instance for processing
		Callable<JobResult> jobCallable = new Job(j);
		Future<JobResult> jobFuture = tpExecutor.submit(jobCallable);

		jobLifeCycle.setAsProcessing(j.getId());

		try {

			JobResult jres = jobFuture.get();
			if (jres.getStatus() == JobResultStatus.SUCCESS) {
				log.info("Succeeded in executing JOB #" + j.getId());
				jobLifeCycle.setAsSucceeded(j.getId());
			} else {
				log.info("Failed on completion JOB #" + j.getId());
				jobLifeCycle.setAsFailed(j.getId());
			}
		} catch (InterruptedException e) {
			log.info("Interrupted Failed while executing JOB #" + j.getId());
			jobLifeCycle.setAsFailed(j.getId());
			e.printStackTrace();
		} catch (ExecutionException e) {
			log.info("Execution Failed while executing JOB #" + j.getId());
			jobLifeCycle.setAsFailed(j.getId());
			e.printStackTrace();
		} catch (Exception e) {
			log.info("Unknown exception while executing JOB #" + j.getId());
			jobLifeCycle.setAsFailed(j.getId());
			e.printStackTrace();
		}
	}

	@PostConstruct
	public void init() {
		tpExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				keepAliveTime, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(workQueueSize));
	}

	@PreDestroy
	public void end() {
		tpExecutor.shutdown();
	}
}

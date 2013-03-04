package org.ajp.server.service;

import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.ajp.server.event.JobEvent;
import org.ajp.server.model.JobRequest;
import org.ajp.server.model.enums.JOBSTATUS;


@Stateless
public class JobLifeCycleService {

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	@Inject
	private Event<JobEvent> jobEventSrc;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobRequest createJob(JobRequest j) {
		log.info("Creating #" + j.getId());
		em.persist(j);
		return j;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobRequest setAsAborted(Long j) {
		JobRequest job = em.find(JobRequest.class, j);
		job.setJobStatus(JOBSTATUS.ABORTED);
		if (em.contains(job))
			em.merge(job);
		else
			em.persist(job);
		return job;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobRequest setAsWaiting(Long j) {
		JobRequest job = em.find(JobRequest.class, j);
		job.setJobStatus(JOBSTATUS.WAITING);
		em.persist(job);
		return job;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobRequest setAsProcessing(Long j) {
		JobRequest job = em.find(JobRequest.class, j);
		job.setJobStatus(JOBSTATUS.PROCESSING);
		em.persist(job);
		return job;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobRequest setAsSucceeded(Long j) {
		JobRequest job = em.find(JobRequest.class, j);
		job.setJobStatus(JOBSTATUS.SUCCEEDED);
		em.persist(job);
		return job;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public JobRequest setAsFailed(Long j) {
		JobRequest job = em.find(JobRequest.class, j);
		job.setJobStatus(JOBSTATUS.FAILED);
		em.persist(job);
		return job;
	}

	@Asynchronous
	public void runAsyncJob(JobRequest j) {
		jobEventSrc.fire(new JobEvent(j));
	}
}

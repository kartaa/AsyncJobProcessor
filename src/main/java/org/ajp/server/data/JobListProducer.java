package org.ajp.server.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.ajp.server.event.JobEvent;
import org.ajp.server.model.JobRequest;
import org.ajp.server.model.enums.JOBTYPE;


@RequestScoped
public class JobListProducer {
	@Inject
	private EntityManager em;

	private List<JobRequest> jobs;

	private int LATEST_JOB_COUNT_LIMIT = 10;

	// @Named provides access the return value via the EL variable name "jobs"
	// in the UI (e.g.,
	// Facelets or JSP view)
	@Produces
	@Named
	public List<JobRequest> getLatestJobs() {
		return jobs;
	}

	@Produces
	@Named
	public List<String> getJobTypes() {
		List<String> list = new ArrayList<String>();
		for (JOBTYPE job : JOBTYPE.values()) {
			list.add(job.toString());
		}
		return list;
	}

	@Asynchronous
	public void onJobListChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS) final JobEvent jobEvent) {
		retrieveLatestJobsOrderedByCreateTime();
	}

	@PostConstruct
	public void retrieveLatestJobsOrderedByCreateTime() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JobRequest> criteria = cb.createQuery(JobRequest.class);
		Root<JobRequest> job = criteria.from(JobRequest.class);
		criteria.select(job).orderBy(cb.desc(job.get("created")));
		jobs = em.createQuery(criteria).setMaxResults(LATEST_JOB_COUNT_LIMIT)
				.getResultList();
	}

}

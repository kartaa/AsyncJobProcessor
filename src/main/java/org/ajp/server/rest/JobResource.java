package org.ajp.server.rest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ajp.server.model.JobRequest;
import org.ajp.server.model.enums.JOBSTATUS;
import org.ajp.server.model.enums.JOBTYPE;
import org.ajp.server.service.JobLifeCycleService;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * Job Resource Rest Services <br/>
 * The apis in this resource take care of the fact that they do not block the
 * client for long and maintain a consistent response time.
 * 
 */
@Path("/jobs")
@RequestScoped
public class JobResource {
	@Inject
	private Logger log;
	@Inject
	private EntityManager em;
	@Inject
	private JobLifeCycleService jobLifeCycle;

	/**
	 * 
	 * 
	 * @return list of the 10 last submitted jobs
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<JobRequest> listLatestJobs() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<JobRequest> criteria = cb.createQuery(JobRequest.class);
		Root<JobRequest> job = criteria.from(JobRequest.class);
		criteria.select(job).orderBy(cb.desc(job.get("created")));
		return em.createQuery(criteria).setMaxResults(10).getResultList();
	}

	/**
	 * Get the job object corresponding to given id. Returns null if job object
	 * is not found
	 * 
	 * @param id
	 * @return job object for requested id
	 */
	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces(MediaType.TEXT_XML)
	public JobRequest lookupJobById(@PathParam("id") long id) {
		return em.find(JobRequest.class, id);
	}

	/**
	 * Get the job status for job corresponding the given id. Returns null if
	 * job object is not found
	 * 
	 * @param id
	 * @return job status for requested id
	 */
	@GET
	@Path("/status/{id:[0-9][0-9]*}")
	@Produces(MediaType.TEXT_XML)
	public JOBSTATUS lookupJobStatusById(@PathParam("id") long id) {
		JobRequest j = em.find(JobRequest.class, id);
		JOBSTATUS jobStatus = null;
		if (j != null)
			jobStatus = j.getJobStatus();
		return jobStatus;
	}

	/**
	 * Accept a job with form parameters describing a job.
	 * <p>
	 * Form description: <br/>
	 * <ul>
	 * <li>"jobtype" - string of JOBTYPE</li>
	 * </ul>
	 * </p>
	 * 
	 * @param input
	 * @param resp
	 * @return new job object created for submitted request
	 */
	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_XML)
	public JobRequest submitJob(MultipartFormDataInput input,
			@Context HttpServletResponse resp) {

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

		List<InputPart> jobTypeParts = uploadForm.get("jobtype");
		resp.setHeader("Refresh", "2; URL=/ajp-server/index.jsf");

		if (!(jobTypeParts.size() == 1)) {
			resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		// Create a new Job Object and use its ID to proceed.
		InputPart jobType = jobTypeParts.get(0);
		JobRequest j = new JobRequest();
		try {
			j.setCreated(new Date());
			j.setJobStatus(JOBSTATUS.CREATED);
			j.setJobType(JOBTYPE.valueOf(jobType.getBodyAsString()));
			j = jobLifeCycle.createJob(j);
		} catch (Exception e1) {
			log.info("Could not create new Job.");
			j = null;
			e1.printStackTrace();
		}
		if (j == null) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return j;
		}

		boolean abortJob = false;
		try {
			jobLifeCycle.runAsyncJob(j);

			log.info("JOBID #" + j.getId()
					+ "Job created and processing started.");
		} catch (Exception e) {
			e.printStackTrace();
			abortJob = true;
		}
		if (abortJob == true) {
			// Remove Job Object
			jobLifeCycle.setAsAborted(j.getId());
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			log.info("JOBID #" + j.getId() + " Aborted.");
		}
		return j;
	}

	/**
	 * Get the results for a job. If a job is in SUCCEEDED state then the
	 * processed result is returned whereas if the job is in any other state
	 * null is returned
	 * 
	 * @param id
	 * @return null or the processed result of job
	 */
	@GET
	@Path("/result/{id:[0-9][0-9]*}")
	@Produces(MediaType.MULTIPART_FORM_DATA)
	public Response getJobResultById(@PathParam("id") long id) {
		JobRequest j = em.find(JobRequest.class, id);
		if (j != null) {
			if (j.getJobStatus() == JOBSTATUS.SUCCEEDED) {
				return Response.ok(new String("Success")).build();
			} else {
				return Response.ok(new String("Failed"))
						.status(Status.NOT_FOUND).build();
			}
		}
		return Response.noContent().status(Status.NO_CONTENT).build();
	}
}

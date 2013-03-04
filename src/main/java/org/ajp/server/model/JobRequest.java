package org.ajp.server.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.ajp.server.model.enums.JOBSTATUS;
import org.ajp.server.model.enums.JOBTYPE;


@Entity
@XmlRootElement
public class JobRequest implements Serializable {
	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	private JOBTYPE jobType;

	@NotNull
	private JOBSTATUS jobStatus;

	@NotNull
	private Date created;

	private Date finished;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JOBTYPE getJobType() {
		return jobType;
	}

	public void setJobType(JOBTYPE jobType) {
		this.jobType = jobType;
	}

	public JOBSTATUS getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JOBSTATUS jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}
}
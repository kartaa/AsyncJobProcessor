package org.ajp.server.model.enums;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum JOBSTATUS {
	ABORTED, CREATED, WAITING, PROCESSING, SUCCEEDED, FAILED, ARCHIVED
}

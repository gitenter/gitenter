package com.gitenter.enzymark.configfile.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
public class GitEnterConfigBean {

	@Getter
	private Integer version;
	
	private List<String> documents;
	private TraceabilityConfigBean traceability;
	
	public List<String> getDocumentScanPaths() {
		return documents;
	}
	
	public boolean isTraceablityScanEnabled() {
		return (traceability != null);
	}
	
	public List<String> getTraceabilityScanPaths(String fileFormat) {
		return traceability.getTraceabilityScanPaths(fileFormat);
	}
}

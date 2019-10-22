package com.gitenter.enzymark.configfile.bean;

import java.util.List;

import lombok.Setter;

@Setter
public class TraceabilityConfigBean {
	
	/*
	 * This is a finite list of traceable files we are
	 * currently supporting.
	 */
	private List<String> markdown;
	private List<String> gherkin;
	private List<String> java;
	
	public List<String> getTraceabilityScanPaths(String fileFormat) {
		switch(fileFormat) {
		
		/*
		 * TODO: Use reflection.
		 */
		case "markdown":
			return markdown;
		case "gherkin":
			return gherkin;
		case "java":
			return java;
			
		/*
		 * TODO:
		 * Should raise illegal operation exception if something else is
		 * queries.
		 */
		default:
			return null;
		}
	}
}

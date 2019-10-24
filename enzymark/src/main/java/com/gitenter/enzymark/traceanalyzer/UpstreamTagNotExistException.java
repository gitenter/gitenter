package com.gitenter.enzymark.traceanalyzer;

public class UpstreamTagNotExistException extends TraceAnalyzerException {

	private static final long serialVersionUID = 1L;
	
	public UpstreamTagNotExistException(String tag, String upstreamTag, TraceableFile document) {
		super("The traceable item \""+tag+ "\" in document with path \""+document.getRelativePath()
				+"\" is referred to another item \""+upstreamTag+"\", "
				+ "but \""+upstreamTag+"\" is not existed throughout the system. "
				+ "(You may want to check if all the document paths are included in the .properties file.)");
	}
}

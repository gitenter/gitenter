package com.gitenter.enzymark.traceanalyzer;

public class ItemTagNotUniqueException extends TraceAnalyzerException {

	private static final long serialVersionUID = 1L;
	
	public ItemTagNotUniqueException(String tag, TraceableFile document1, TraceableFile document2) {
		super("The traceable item tag \""+tag
				+"\" is defined in both \""+document1.getRelativePath()
				+"\" and \""+document2.getRelativePath()+"\".");
	}
}

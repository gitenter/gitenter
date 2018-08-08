package com.gitenter.enzymark.traceanalyzer;

public class ItemTagNotUniqueException extends TraceAnalyzerException {

	private static final long serialVersionUID = 1L;
	
	public ItemTagNotUniqueException(String tag, TraceableDocument document1, TraceableDocument document2) {
		super("The traceable item tag \""+tag
				+"\" is defined in both \""+document1.getRelativeFilepath()
				+"\" and \""+document2.getRelativeFilepath()+"\".");
	}
}

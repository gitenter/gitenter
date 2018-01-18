package enterovirus.enzymark.traceanalyzer;

public class UpstreamTagNotExistException extends TraceAnalyzerException {

	private static final long serialVersionUID = 1L;
	
	public UpstreamTagNotExistException(String tag, String upstreamTag, TraceableDocument document) {
		super("The traceable item "+tag
				+" is referred to another item "+upstreamTag+", "
				+ "but "+upstreamTag+" is not existed throughout the system. "
				+ "(Check if all the document folders are setup in the .properties file.)");
	}
}

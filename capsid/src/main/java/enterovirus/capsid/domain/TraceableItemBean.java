package enterovirus.capsid.domain;

public class TraceableItemBean extends LineContentBean {

	private String traceableTag;
	
	public TraceableItemBean(Integer lineNumber, String content) {
		super(lineNumber, content);
	}
}

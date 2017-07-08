package enterovirus.capsid.domain;

public class LineContentBean {

	private Integer lineNumber;
	private String content;
	
	public LineContentBean(Integer lineNumber, String content) {
		this.lineNumber = lineNumber;
		this.content = content;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}

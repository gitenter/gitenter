package enterovirus.capsid.domain;

import java.util.*;

public class TextFileBean {
	
	private String filePath;
	private List<LineContentBean> lineContents = new ArrayList<LineContentBean>();
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public List<LineContentBean> getLineContents() {
		return lineContents;
	}
	public void setLineContents(List<LineContentBean> lineContents) {
		this.lineContents = lineContents;
	}
	
	public void addLineContent(LineContentBean lineContent) {
		lineContents.add(lineContent);
	}
	
//	private String content;
//
//	public String getContent() {
//		return content;
//	}
//
//	public void setContent(String content) {
//		this.content = content;
//	}
}

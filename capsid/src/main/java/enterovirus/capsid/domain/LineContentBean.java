package enterovirus.capsid.domain;

import lombok.*;

@Getter
@Setter
public class LineContentBean {

	private Integer lineNumber;
	private String content;
	
	/*
	 * TODO:
	 * 
	 * LineContentBean should link back to TextFileBean.
	 * But that will cause error of Jackson 2 to transfer to JSON
	 * because loop exists. Should setup Jaskson (maybe by annotation?)
	 * to specify that.
	 */
	
	public LineContentBean(Integer lineNumber, String content) {
		this.lineNumber = lineNumber;
		this.content = content;
	}
}

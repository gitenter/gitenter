package enterovirus.protease.domain;

import lombok.*;

/*
 * TODO:
 * This one is currently only used for
 * capsid/src/main/webapp/WEB-INF/views/git-navigation/document.jsp
 * 
 * TraceableItemBean cannot extend this one,
 * since that is coming from Hibernate, for which the
 * Java annotation cannot be set in here.
 * 
 * Consider completely remove this bean. If it is for display
 * only, we can implement it in a much easier way.
 * (the only relevant part is DocumentImpl.java).
 */
@Getter
@Setter
public class LineContentBean {

	private Integer lineNumber;
	private String content;
	
	/*
	 * TODO:
	 * 
	 * LineContentBean should link back to DocumentBean.
	 * But that will cause error of Jackson 2 to transfer to JSON
	 * because loop exists. Should setup Jaskson (maybe by annotation?)
	 * to specify that.
	 */
	
	public LineContentBean(Integer lineNumber, String content) {
		this.lineNumber = lineNumber;
		this.content = content;
	}
}

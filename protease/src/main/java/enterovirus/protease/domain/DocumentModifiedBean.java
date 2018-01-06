package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "document_modified")
public class DocumentModifiedBean extends DocumentBean {
	
	@Column(name="relative_filepath", updatable=false)
	private String relativeFilepath;
	
	@OneToMany(targetEntity=TraceableItemBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="originalDocument")
	private List<TraceableItemBean> traceableItems = new ArrayList<TraceableItemBean>();
	
	/*
	 * @Transient is to specify that the property or field is not persistent.
	 * 
	 * TODO:
	 * Since TraceableItemBean cannot extend LineContentBean (because of JPA
	 * annotation), should we completely remove this one, and replace it with
	 * "String blobContent"?
	 */
	@Transient
//	private List<LineContentBean> lineContents = new ArrayList<LineContentBean>();
	private String content;
	
	/*
	 * This default constructor is needed for Hibernate.
	 */
	public DocumentModifiedBean () {
		
	}
	
	public DocumentModifiedBean (CommitBean commit, String relativeFilepath) {
		super(commit);
		this.relativeFilepath = relativeFilepath;
	}
	
	public boolean addTraceableItem(TraceableItemBean traceableItem) {
		return traceableItems.add(traceableItem);
	}
	
	/*
	 * TODO:
	 * This function should work for general documents, rather
	 * than the just modified ones. However, that depends on the
	 * detail of markdown visualization strategy.
	 */
	public List<LineContent> getLineContents () {

		List<LineContent> lineContents = new ArrayList<LineContent>();
	
		/*
		 * TODO: 
		 * Split by "newline" which is compatible to Windows
		 * or Linux formats.
		 */
		int lineNumber = 1;
		for (String content : content.split("\n")) {
			lineContents.add(new LineContent(new Integer(lineNumber), content));
			++lineNumber;
		}
		
		return lineContents;
	}
	
	@Getter
	@Setter
	public class LineContent {

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
		
		public LineContent(Integer lineNumber, String content) {
			this.lineNumber = lineNumber;
			this.content = content;
		}
	}
}
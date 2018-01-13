package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

/*
 * In SQL, use "Joined" inheritance Strategy out of four strategies
 * (1) Mapped Superclass
 * (2) Table per Class
 * (3) Single Table
 * (4) Joined
 */
@Getter
@Setter
@Entity
@Table(schema = "git", name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="commit_id")
	private CommitValidBean commit;
	
	@Column(name="relative_filepath", updatable=false)
	private String relativeFilepath;
	
	@OneToMany(targetEntity=TraceableItemBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="document")
	private List<TraceableItemBean> traceableItems = new ArrayList<TraceableItemBean>();
	
	/*
	 * @Transient is to specify that the property or field is not persistent.
	 */
	@Transient
	private byte[] blobContent;

	/*
	 * This default constructor is needed for Hibernate.
	 */
	public DocumentBean () {
		
	}
	
	public DocumentBean (CommitValidBean commit, String relativeFilepath) {
		this.commit = commit;
		this.relativeFilepath = relativeFilepath;
	}
	
	public boolean addTraceableItem(TraceableItemBean traceableItem) {
		return traceableItems.add(traceableItem);
	}
	
	public String getContent () {
		return new String(blobContent);
	}
	
	public void setContent (String content) {
		blobContent = content.getBytes();
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
		for (String content : getContent().split("\n")) {
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
	
	/*
	 * Use together with buildTraceableItemIndex()
	 */
	@Transient
	private Map<String,TraceableItemBean> traceableItemMap;
	
	public void buildTraceableItemIndex() {
		
		traceableItemMap = new HashMap<String,TraceableItemBean>();
		
		for (TraceableItemBean item : traceableItems) {
			traceableItemMap.put(item.getItemTag(), item);
		}
	}
	
	public TraceableItemBean getTraceableItem (String itemTag) {
		return traceableItemMap.get(itemTag);
	}
}
package com.gitenter.protease.domain.git;

import java.io.IOException;
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
import javax.validation.constraints.NotNull;

import org.eclipse.jgit.api.errors.GitAPIException;

import lombok.Getter;
import lombok.Setter;

/*
 * It seems if we extend another bean, the attributes
 * in the superclass automatically becomes @Transient.
 */
@Getter
@Setter
@Entity
@Table(schema = "git", name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentBean extends FileBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="commit_id")
	private ValidCommitBean commit;
	
	@NotNull
	@Column(name="relative_path", updatable=false)
	private String relativePath;
	
	@OneToMany(targetEntity=TraceableItemBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="document")
	private List<TraceableItemBean> traceableItems = new ArrayList<TraceableItemBean>();
	
	/*
	 * Attributes in superclass act exactly the same as attributes
	 * with annotation @Transient.
	 */
	
	public String getContent () throws IOException, GitAPIException {
		return new String(getBlobContent());
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
	
	public boolean addTraceableItem(TraceableItemBean traceableItem) {
		return traceableItems.add(traceableItem);
	}
	
	/*
	 * TODO:
	 * Build "traceableItemMap" by some proxy pattern triggered by this method.
	 * Remove "buildTraceableItemIndex".
	 */
	public TraceableItemBean getTraceableItem(String itemTag) {
		return traceableItemMap.get(itemTag);
	}
	
	/*
	 * TODO:
	 * This function should work for general documents, rather
	 * than the just modified ones. However, that depends on the
	 * detail of markdown visualization strategy.
	 */
//	public List<LineContent> getLineContents () throws IOException, GitAPIException {
//
//		List<LineContent> lineContents = new ArrayList<LineContent>();
//	
//		/*
//		 * TODO: 
//		 * Split by "newline" which is compatible to Windows
//		 * or Linux formats.
//		 */
//		int lineNumber = 1;
//		for (String content : getContent().split("\n")) {
//			lineContents.add(new LineContent(new Integer(lineNumber), content));
//			++lineNumber;
//		}
//		
//		return lineContents;
//	}
//	
//	/*
//	 * "static" because otherwise there may be potential memory 
//	 * leaking (when "DocumentBean" is discarded after "LineContent" 
//	 * is created). 
//	 */
//	@Getter
//	@Setter
//	public static class LineContent {
//
//		private Integer lineNumber;
//		private String content;
//		
//		/*
//		 * TODO:
//		 * 
//		 * LineContentBean should link back to DocumentBean.
//		 * But that will cause error of Jackson 2 to transfer to JSON
//		 * because loop exists. Should setup Jaskson (maybe by annotation?)
//		 * to specify that.
//		 */
//		
//		public LineContent(Integer lineNumber, String content) {
//			this.lineNumber = lineNumber;
//			this.content = content;
//		}
//	}
}
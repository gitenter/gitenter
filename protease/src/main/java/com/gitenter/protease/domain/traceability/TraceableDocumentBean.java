package com.gitenter.protease.domain.traceability;

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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.git.DocumentBean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/*
 * It seems if we extend another bean, the attributes
 * in the superclass automatically becomes @Transient.
 */
@Getter
@Setter
@Entity
@Table(schema = "traceability", name = "traceable_document")
public class TraceableDocumentBean implements ModelBean {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@OneToOne(targetEntity=DocumentBean.class, fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name="id", referencedColumnName="id")
    private DocumentBean document;
	
	@OneToMany(targetEntity=TraceableItemBean.class, mappedBy="traceableDocument",
			fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<TraceableItemBean> traceableItems = new ArrayList<TraceableItemBean>();
	
	public boolean addTraceableItem(TraceableItemBean traceableItem) {
		return traceableItems.add(traceableItem);
	}

	@Transient
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Map<String,TraceableItemBean> traceableItemMap;
	
	private void buildTraceableItemMap() {
		
		traceableItemMap = new HashMap<String,TraceableItemBean>();
		
		for (TraceableItemBean item : traceableItems) {
			traceableItemMap.put(item.getItemTag(), item);
		}
	}
	
	public TraceableItemBean getTraceableItem(String itemTag) {
		
		if (traceableItemMap == null) {
			buildTraceableItemMap();
		}
		
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
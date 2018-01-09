package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "traceable_item")
public class TraceableItemBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="original_document_id")
	private DocumentModifiedBean originalDocument;
	
	@Column(name="item_tag", updatable=false)
	private String itemTag;
	
	@Column(name="content", updatable=false)
	private String content;
	
	@OneToMany(targetEntity=TraceabilityMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="upstreamItem")
	private List<TraceabilityMapBean> downstreamMaps = new ArrayList<TraceabilityMapBean>();

	@OneToMany(targetEntity=TraceabilityMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="downstreamItem")
	private List<TraceabilityMapBean> upstreamMaps = new ArrayList<TraceabilityMapBean>();
	
	/*
	 * Hibernate constructor
	 */
	public TraceableItemBean () {
		
	}

	public TraceableItemBean(DocumentModifiedBean document, String itemTag, String content) {
		this.originalDocument = document;
		this.itemTag = itemTag;
		this.content = content;
	}
	
	public boolean addDownstreamMap (TraceabilityMapBean map) {
		return downstreamMaps.add(map);
	}
	
	public boolean addUpstreamMap (TraceabilityMapBean map) {
		return upstreamMaps.add(map);
	}
	
	public List<TraceabilityMapBean.TraceableItemDocumentPair> getUpstreamPairs () {
		
		List<TraceabilityMapBean.TraceableItemDocumentPair> pairs = new ArrayList<TraceabilityMapBean.TraceableItemDocumentPair>();
		
		for (TraceabilityMapBean map : upstreamMaps) {
			pairs.add(map.getUpstreamPair());
		}
		return pairs;
	}
	
	public List<TraceabilityMapBean.TraceableItemDocumentPair> getDownstreamPairs () {
		
		List<TraceabilityMapBean.TraceableItemDocumentPair> pairs = new ArrayList<TraceabilityMapBean.TraceableItemDocumentPair>();
		
		for (TraceabilityMapBean map : downstreamMaps) {
			pairs.add(map.getDownstreamPair());
		}
		return pairs;
	}
}

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
	@JoinColumn(name="document_id")
	private DocumentBean document;
	
	@Column(name="item_tag", updatable=false)
	private String itemTag;
	
	@Column(name="content", updatable=false)
	private String content;
	
	/*
	 * Here I am not using @ManyToMany for several reasons:
	 * 
	 * (1) "git.traceability_map" has the "id" field, so other tables
	 * e.g. the review tables may refer to this relationship. Therefore 
	 * the @ManyToMany annotation cannot really have its full functions.
	 * (or If I remember it right, it simply cannot be down.
	 * 
	 * (2) Unfortunately, @ManyToMany relationship does not support orphonRemoval,
	 * So using it will cause further complicity.
	 * > Only relationships with single cardinality on the source side can enable orphan removal, 
	 * > which is why the orphanRemoval option is defined on the @OneToOne and @OneToMany 
	 * > relationship annotations, but on neither of the @ManyToOne or @ManyToMany annotations.
	 * > (book: "Pro JPA 2")
	 */
	@OneToMany(targetEntity=TraceabilityMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="upstreamItem")
	private List<TraceabilityMapBean> downstreamMaps = new ArrayList<TraceabilityMapBean>();

	@OneToMany(targetEntity=TraceabilityMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="downstreamItem")
	private List<TraceabilityMapBean> upstreamMaps = new ArrayList<TraceabilityMapBean>();
	
	/*
	 * Hibernate constructor
	 */
	public TraceableItemBean () {
		
	}

	public TraceableItemBean(DocumentBean document, String itemTag, String content) {
		this.document = document;
		this.itemTag = itemTag;
		this.content = content;
	}
	
	public boolean addDownstreamMap (TraceabilityMapBean map) {
		return downstreamMaps.add(map);
	}
	
	public boolean addUpstreamMap (TraceabilityMapBean map) {
		return upstreamMaps.add(map);
	}
	
	public List<TraceableItemBean> getDownstreamItems () {
		
		List<TraceableItemBean> items = new ArrayList<TraceableItemBean>();
		for (TraceabilityMapBean map : downstreamMaps) {
			items.add(map.getDownstreamItem());
		}
		return items;
	}
	
	public List<TraceableItemBean> getUpstreamItems () {
		
		List<TraceableItemBean> items = new ArrayList<TraceableItemBean>();
		for (TraceabilityMapBean map : upstreamMaps) {
			items.add(map.getUpstreamItem());
		}
		return items;
	}
}

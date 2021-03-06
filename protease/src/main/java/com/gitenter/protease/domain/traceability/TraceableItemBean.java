package com.gitenter.protease.domain.traceability;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.ModelBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "traceability", name = "traceable_item")
public class TraceableItemBean implements ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="traceable_document_id")
	private TraceableDocumentBean traceableDocument;
	
	@NotNull
	@Column(name="item_tag", updatable=false)
	private String itemTag;
	
	@Column(name="content", updatable=false)
	private String content;
	
	/*
	 * Note:
	 * 
	 * @ManyToMany relationship does not support orphonRemoval.
	 * 
	 * > Only relationships with single cardinality on the source side can enable orphan removal, 
	 * > which is why the orphanRemoval option is defined on the @OneToOne and @OneToMany 
	 * > relationship annotations, but on neither of the @ManyToOne or @ManyToMany annotations.
	 * > (book: "Pro JPA 2")
	 */

	@ManyToMany(targetEntity=TraceableItemBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(schema="traceability", name="traceability_map", 
			joinColumns=@JoinColumn(name="downstream_item_id"), 
			inverseJoinColumns=@JoinColumn(name="upstream_item_id"))
	private List<TraceableItemBean> downstreamItems = new ArrayList<TraceableItemBean>();

	@ManyToMany(mappedBy="downstreamItems")
	private List<TraceableItemBean> upstreamItems = new ArrayList<TraceableItemBean>();

	public boolean addDownstreamItem(TraceableItemBean item) {
		return downstreamItems.add(item);
	}

	public boolean addUpstreamItem(TraceableItemBean item) {
		return upstreamItems.add(item);
	}
}

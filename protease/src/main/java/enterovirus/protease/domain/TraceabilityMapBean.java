package enterovirus.protease.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "traceability_map")
public class TraceabilityMapBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="upstream_document_id")
	private DocumentBean upstreamDocument;
	
	@ManyToOne
	@JoinColumn(name="upstream_item_id")
	private TraceableItemBean upstreamItem;
	
	@ManyToOne
	@JoinColumn(name="downstream_document_id")
	private DocumentBean downstreamDocument;
	
	@ManyToOne
	@JoinColumn(name="downstream_item_id")
	private TraceableItemBean downstreamItem;
	
	/*
	 * Hibernate constructor
	 */
	public TraceabilityMapBean () {
		
	}

	public TraceabilityMapBean(
			DocumentBean upstreamDocument, 
			TraceableItemBean upstreamItem,
			DocumentBean downstreamDocument, 
			TraceableItemBean downstreamItem) {
		
		this.upstreamDocument = upstreamDocument;
		this.upstreamItem = upstreamItem;
		this.downstreamDocument = downstreamDocument;
		this.downstreamItem = downstreamItem;
	}
	
	@Getter
	public class TraceableItemDocumentPair {
		
		private DocumentBean document;
		private TraceableItemBean traceableItem;
	
		private TraceableItemDocumentPair(DocumentBean document, TraceableItemBean traceableItem) {
			this.document = document;
			this.traceableItem = traceableItem;
		}
	}

	public TraceableItemDocumentPair getUpstreamPair () {
		return new TraceableItemDocumentPair(upstreamDocument, upstreamItem);
	}
	
	public TraceableItemDocumentPair getDownstreamPair () {
		return new TraceableItemDocumentPair(downstreamDocument, downstreamItem);
	}
}

package enterovirus.protease.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
	private DocumentModifiedBean document;
	
	@Column(name="line_number", updatable=false)
	private Integer lineNumber;
	
	@Column(name="item_tag", updatable=false)
	private String itemTag;
	
	@Column(name="content", updatable=false)
	private String content;
	
	/*
	 * Hibernate constructor
	 */
	public TraceableItemBean () {
		
	}

	public TraceableItemBean(DocumentModifiedBean document, Integer lineNumber, String itemTag, String content) {
		this.document = document;
		this.lineNumber = lineNumber;
		this.itemTag = itemTag;
		this.content = content;
	}
}

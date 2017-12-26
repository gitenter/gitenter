package enterovirus.protease.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "document_unmodified")
public class DocumentUnmodifiedBean extends DocumentBean {
	
	@ManyToOne
	@JoinColumn(name="original_document_id")
	private DocumentModifiedBean originalDocument;
}
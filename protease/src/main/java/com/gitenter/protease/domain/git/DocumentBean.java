package com.gitenter.protease.domain.git;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.review.InReviewDocumentBean;
import com.gitenter.protease.domain.traceability.TraceableDocumentBean;

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
public class DocumentBean extends IncludeFileBean implements ModelBean {
	
	/*
	 * Cannot use `@Inheritance(strategy = InheritanceType.JOINED)`/subclass
	 * to populate it. The fundamental reason is because a `DocumentBean` 
	 * can be `TraceableDocumentBean` and `InReviewDocumentBean` at the same
	 * time, and Java does not support mixin.
	 */
	@OneToOne(targetEntity=TraceableDocumentBean.class, mappedBy = "document", 
			optional=true, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private TraceableDocumentBean traceableDocument;
	
	@OneToOne(targetEntity=InReviewDocumentBean.class, mappedBy = "document", 
			optional=true, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private InReviewDocumentBean inReviewDocument;
}
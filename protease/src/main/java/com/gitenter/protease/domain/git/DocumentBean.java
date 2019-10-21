package com.gitenter.protease.domain.git;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.gitenter.protease.domain.ModelBean;

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
public class DocumentBean extends IncludeFileBean implements ModelBean {
	
	/*
	 * TODO:
	 * Cannot use `@Inheritance(strategy = InheritanceType.JOINED)`/subclass
	 * to populate it. The fundamental reason is because a `DocumentBean` 
	 * can be `TraceableDocumentBean` and `InReviewDocumentBean` at the same
	 * time, and Java does not support mixin.
	 */
}
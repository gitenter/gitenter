package com.gitenter.protease.domain.review;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.git.ValidCommitBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "subsection")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SubsectionBean extends ValidCommitBean implements ModelBean {

	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="review_id")
	private ReviewBean review;
	
	/*
	 * Unidirectional
	 */
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private UserBean projectOrganizer;
	
	@NotNull
	@Column(name="create_at", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;
	
	@OneToMany(targetEntity=InReviewDocumentBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="subsection")
	private List<InReviewDocumentBean> inReviewDocuments;
}

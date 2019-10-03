package com.gitenter.protease.domain.review;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.git.DocumentBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "in_review_document")
public class InReviewDocumentBean extends DocumentBean implements ModelBean {
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="subsection_id")
	private SubsectionBean subsection;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="previous_version_id")
	private InReviewDocumentBean previousVersion;
	
	@NotNull
	@Column(name="status_shortname")
	@Convert(converter = ReviewStatusConventer.class)
	private ReviewStatus status;
	
	@NotNull
	@Column(name="status_setup_at", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date statusSetupAt;
	
	@OneToMany(targetEntity=DiscussionTopicBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="document")
	private List<DiscussionTopicBean> discussionTopics;
	
	@OneToMany(targetEntity=VoteBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="document")
	private List<VoteBean> votes;
}

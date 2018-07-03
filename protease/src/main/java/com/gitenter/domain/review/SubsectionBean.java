package com.gitenter.domain.review;

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

import com.gitenter.domain.auth.MemberBean;
import com.gitenter.domain.git.ValidCommitBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "subsection")
@Inheritance(strategy = InheritanceType.JOINED)
abstract public class SubsectionBean extends ValidCommitBean {

	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="review_id")
	private ReviewBean review;
	
	/*
	 * Unidirectional
	 */
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="member_id")
	private MemberBean projectOrganizer;
	
	@NotNull
	@Column(name="create_at", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;
	
	@OneToMany(targetEntity=InReviewDocumentBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="subsection")
	private List<InReviewDocumentBean> inReviewDocuments;
}

package com.gitenter.protease.domain.review;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.ModelBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "discussion_subsection")
public class DiscussionSubsectionBean extends SubsectionBean implements ModelBean {

	@NotNull
	@Column(name="deadline", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date deadline;
	
	@OneToMany(targetEntity=ReviewMeetingBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="subsection")
	private List<ReviewMeetingBean> reviewMeetings;
}

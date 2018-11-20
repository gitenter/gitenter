package com.gitenter.protease.domain.review;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.gitenter.protease.domain.ModelBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "reviewer")
public class ReviewerBean extends AttendeeBean implements ModelBean {

	@Column(name="liability_description")
	private String liabilityDescription;
	
	@OneToMany(targetEntity=VoteBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="reviewer")
	private List<VoteBean> votes;
}

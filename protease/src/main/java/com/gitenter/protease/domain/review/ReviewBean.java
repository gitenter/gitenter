package com.gitenter.protease.domain.review;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "review")
public class ReviewBean implements ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="repository_id")
	private RepositoryBean repository;
	
	/*
	 * TODO:
	 * Relate it to tag?
	 */
	@NotNull
	@Column(name="version_number", updatable=false)
	private String versionNumber;
	
	@Column(name="description", updatable=false)
	private String description;
	
	@OneToMany(targetEntity=SubsectionBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="review")
	private List<SubsectionBean> subsections;
	
	@OneToMany(targetEntity=AttendeeBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="review")
	private List<AttendeeBean> attendees;
}

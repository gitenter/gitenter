package com.gitenter.protease.domain.review;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.auth.PersonBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "attendee")
@Inheritance(strategy = InheritanceType.JOINED)
public class AttendeeBean implements ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="review_id")
	private ReviewBean review;
	
	/*
	 * Unidirectional
	 */
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="person_id")
	private PersonBean person;
	
	@ManyToMany(mappedBy="attendees", fetch=FetchType.LAZY)
	private List<ReviewMeetingBean> reviewMeetings;
}

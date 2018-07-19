package com.gitenter.protease.domain.review;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "review_meeting")
public class ReviewMeetingBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="subsection_id")
	private DiscussionSubsectionBean subsection;
	
	@ManyToMany(targetEntity=AttendeeBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(schema="review", name="review_meeting_attendee_map", 
			joinColumns=@JoinColumn(name="attendee_id"), 
			inverseJoinColumns=@JoinColumn(name="review_meeting_id"))
	private List<AttendeeBean> attendees;
	
	@NotNull
	@Column(name="start_at", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startAt;
}

package com.gitenter.domain.review;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
@Table(schema = "review", name = "review_meeting_record")
public class ReviewMeetingRecordBean extends DiscussionTopicBean {

	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="review_meeting_id")
	private ReviewMeetingBean reviewMeeting;
	
	@Column(name="content", updatable=false)
	private String content;
	
	@NotNull
	@Column(name="record_at", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date recordAt;
}

package com.gitenter.domain.review;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(schema = "review", name = "comment")
public class CommentBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="discussion_topic_id")
	private OnlineDiscussionTopicBean discussionTopic;
	
	/*
	 * Unidirectional
	 */
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="attendee_id")
	private AttendeeBean attendee;
	
	@Column(name="context", updatable=false)
	private String context;
	
	@NotNull
	@Column(name="comment_at", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date commentAt;
}

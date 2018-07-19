package com.gitenter.protease.domain.review;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "review", name = "online_discussion_topic")
public class OnlineDiscussionTopicBean extends DiscussionTopicBean {

	@OneToMany(targetEntity=CommentBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="discussionTopic")
	private List<CommentBean> comments;
}

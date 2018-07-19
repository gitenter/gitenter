package com.gitenter.protease.dao.review;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.review.ReviewMeetingBean;

public interface ReviewMeetingRepository extends PagingAndSortingRepository<ReviewMeetingBean, Integer> {

	Optional<ReviewMeetingBean> findById(Integer id);
}

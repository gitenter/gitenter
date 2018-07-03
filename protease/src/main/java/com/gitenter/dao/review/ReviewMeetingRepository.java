package com.gitenter.dao.review;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.domain.review.ReviewMeetingBean;

public interface ReviewMeetingRepository extends PagingAndSortingRepository<ReviewMeetingBean, Integer> {

	Optional<ReviewMeetingBean> findById(Integer id);
}

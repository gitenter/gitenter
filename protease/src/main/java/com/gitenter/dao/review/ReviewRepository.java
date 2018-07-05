package com.gitenter.dao.review;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.domain.review.ReviewBean;

public interface ReviewRepository extends PagingAndSortingRepository<ReviewBean, Integer> {

	Optional<ReviewBean> findById(Integer id);
}

package com.gitenter.dao.review;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.domain.review.AttendeeBean;

public interface AttendeeRepository extends PagingAndSortingRepository<AttendeeBean, Integer> {

	Optional<AttendeeBean> findById(Integer id);
}

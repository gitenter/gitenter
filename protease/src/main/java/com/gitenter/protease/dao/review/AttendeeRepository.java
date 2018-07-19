package com.gitenter.protease.dao.review;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.review.AttendeeBean;

public interface AttendeeRepository extends PagingAndSortingRepository<AttendeeBean, Integer> {

	Optional<AttendeeBean> findById(Integer id);
}

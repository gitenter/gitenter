package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.MemberBean;

public interface MemberRepository extends PagingAndSortingRepository<MemberBean, Integer> {

	public Optional<MemberBean> findById(Integer id);
	public List<MemberBean> findByUsername(String username);
	
	/*
	 * In Spring Data JPA, save() does both jobs of INSERT and UPDATE,
	 * depend on whether the primary key is the same or not. 
	 * 
	 * The difference between save() and saveAndFlush() is 
	 * saveAndFlush() will commit immediately, while save() will only 
	 * comment when commit() or flush(). */
	public MemberBean saveAndFlush(MemberBean member);
	
	public void delete(MemberBean member);
}

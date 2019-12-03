package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.UserBean;

public interface UserRepository extends PagingAndSortingRepository<UserBean, Integer> {

	public Optional<UserBean> findById(Integer id);
	public List<UserBean> findByUsername(String username);
	
	/*
	 * In Spring Data JPA, save() does both jobs of INSERT and UPDATE,
	 * depend on whether the primary key is the same or not. 
	 * 
	 * The difference between save() and saveAndFlush() is 
	 * saveAndFlush() will commit immediately, while save() will only 
	 * comment when commit() or flush(). */
	public UserBean saveAndFlush(UserBean user);
	
	public void delete(UserBean user);
}

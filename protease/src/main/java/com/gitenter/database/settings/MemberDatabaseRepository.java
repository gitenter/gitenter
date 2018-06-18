package com.gitenter.database.settings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.domain.settings.MemberBean;

interface MemberDatabaseRepository extends PagingAndSortingRepository<MemberBean, Integer> {

	Optional<MemberBean> findById(Integer id);
	List<MemberBean> findByUsername(String username);
	
	/* In Spring Data JPA, save() does both jobs of INSERT and UPDATE,
	 * depend on whether the primary key is the same or not. 
	 * 
	 * The difference between save() and saveAndFlush() is 
	 * saveAndFlush() will commit immediately, while save() will only 
	 * comment when commit() or flush(). */
	MemberBean saveAndFlush(MemberBean member);
}

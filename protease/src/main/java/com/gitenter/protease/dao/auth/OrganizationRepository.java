package com.gitenter.protease.dao.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.OrganizationBean;

public interface OrganizationRepository extends PagingAndSortingRepository<OrganizationBean, Integer> {

	public Optional<OrganizationBean> findById(Integer id);
	public List<OrganizationBean> findByName(String name);
	
	public OrganizationBean saveAndFlush(OrganizationBean organization);
	
	public void delete(OrganizationBean organization);
}

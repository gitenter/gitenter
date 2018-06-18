package com.gitenter.database.settings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.domain.settings.OrganizationBean;

interface OrganizationDatabaseRepository extends PagingAndSortingRepository<OrganizationBean, Integer> {

	Optional<OrganizationBean> findById(Integer id);
	List<OrganizationBean> findByName(String name);
	
	OrganizationBean saveAndFlush(OrganizationBean organization);
}
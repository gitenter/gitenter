package com.gitenter.protease.dao.auth;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;

public interface OrganizationMemberMapRepository extends PagingAndSortingRepository<OrganizationMemberMapBean, Integer> {

	public OrganizationMemberMapBean saveAndFlush(OrganizationMemberMapBean map);
}

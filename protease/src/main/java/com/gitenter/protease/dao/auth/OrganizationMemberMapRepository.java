package com.gitenter.protease.dao.auth;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;

public interface OrganizationMemberMapRepository extends PagingAndSortingRepository<OrganizationMemberMapBean, Integer> {

	OrganizationMemberMapBean saveAndFlush(OrganizationMemberMapBean member);
}

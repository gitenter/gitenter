package com.gitenter.protease.dao.auth;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.SshKeyBean;

public interface SshKeyRepository extends PagingAndSortingRepository<SshKeyBean, Integer> {
	
	public SshKeyBean saveAndFlush(SshKeyBean sshKey);
}
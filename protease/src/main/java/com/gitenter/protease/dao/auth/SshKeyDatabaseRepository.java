package com.gitenter.protease.dao.auth;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.SshKeyBean;

public interface SshKeyDatabaseRepository extends PagingAndSortingRepository<SshKeyBean, Integer> {
	
	SshKeyBean saveAndFlush(SshKeyBean sshKey);
}

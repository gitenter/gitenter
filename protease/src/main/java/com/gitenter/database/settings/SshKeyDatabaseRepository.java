package com.gitenter.database.settings;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.domain.settings.SshKeyBean;

public interface SshKeyDatabaseRepository extends PagingAndSortingRepository<SshKeyBean, Integer> {

	SshKeyBean saveAndFlush(SshKeyBean sshKey);
}

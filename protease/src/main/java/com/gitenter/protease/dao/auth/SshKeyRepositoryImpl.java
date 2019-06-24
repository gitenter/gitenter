package com.gitenter.protease.dao.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitenter.protease.domain.auth.SshKeyBean;

@Repository
class SshKeyRepositoryImpl implements SshKeyRepository {

	@Autowired private SshKeyDatabaseRepository sshKeyDbRepository;
	
	public SshKeyBean saveAndFlush(SshKeyBean sshKey) throws IOException {
		sshKeyDbRepository.saveAndFlush(sshKey);		
		return sshKey;
	}
}

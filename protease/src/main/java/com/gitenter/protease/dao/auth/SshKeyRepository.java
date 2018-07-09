package com.gitenter.protease.dao.auth;

import java.io.IOException;

import com.gitenter.protease.domain.auth.SshKeyBean;

public interface SshKeyRepository {

	public SshKeyBean saveAndFlush(SshKeyBean sshKey) throws IOException;
}

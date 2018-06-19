package com.gitenter.database.auth;

import java.io.IOException;

import com.gitenter.domain.auth.SshKeyBean;

public interface SshKeyRepository {

	public SshKeyBean saveAndFlush(SshKeyBean sshKey, String username) throws IOException;
}

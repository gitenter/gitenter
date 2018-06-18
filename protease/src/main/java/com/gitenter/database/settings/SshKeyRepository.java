package com.gitenter.database.settings;

import java.io.IOException;

import com.gitenter.domain.settings.SshKeyBean;

public interface SshKeyRepository {

	public SshKeyBean saveAndFlush(SshKeyBean sshKey, String username) throws IOException;
}

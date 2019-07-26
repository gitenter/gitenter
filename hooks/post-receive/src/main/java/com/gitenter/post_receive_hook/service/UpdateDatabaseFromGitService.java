package com.gitenter.post_receive_hook.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface UpdateDatabaseFromGitService {

	public void update(HookInputSet input) throws IOException, GitAPIException;
}

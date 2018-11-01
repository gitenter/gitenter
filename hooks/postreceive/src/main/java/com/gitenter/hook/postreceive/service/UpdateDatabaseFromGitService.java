package com.gitenter.hook.postreceive.service;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface UpdateDatabaseFromGitService {

	public void update (HookInputSet input) throws IOException, GitAPIException;
}

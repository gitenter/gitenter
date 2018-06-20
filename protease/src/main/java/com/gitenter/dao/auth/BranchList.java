package com.gitenter.dao.auth;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.domain.git.BranchBean;

public interface BranchList {
	
	Collection<BranchBean> getBranches() throws IOException, GitAPIException;
}

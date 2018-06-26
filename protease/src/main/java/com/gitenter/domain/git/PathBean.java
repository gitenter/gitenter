package com.gitenter.domain.git;

import com.gitenter.gitar.GitPath;

import lombok.Getter;

public abstract class PathBean {

	@Getter
	final protected String relativePath;
	
	@Getter
	final protected CommitBean commit;

	public PathBean(GitPath gitPath, CommitBean commit) {
		this.relativePath = gitPath.getRelativePath();
		this.commit = commit;
	}
}

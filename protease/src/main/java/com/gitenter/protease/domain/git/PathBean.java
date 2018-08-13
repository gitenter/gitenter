package com.gitenter.protease.domain.git;

import com.gitenter.gitar.GitHistoricalPath;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PathBean {

	protected String relativePath;
	
	protected String name;
	
	protected ValidCommitBean commit;
	
	public void setFromGit(GitHistoricalPath gitPath) {
		relativePath = gitPath.getRelativePath();
		name = gitPath.getName();
	}
}

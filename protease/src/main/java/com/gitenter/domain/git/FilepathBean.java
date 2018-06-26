package com.gitenter.domain.git;

import com.gitenter.gitar.GitFilepath;

public class FilepathBean extends PathBean {
	
	public FilepathBean(GitFilepath gitFilepath, CommitBean commit) {
		super(gitFilepath, commit);
	}
}

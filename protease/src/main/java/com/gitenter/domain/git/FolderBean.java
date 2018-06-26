package com.gitenter.domain.git;

import java.util.ArrayList;
import java.util.Collection;

import com.gitenter.gitar.GitFilepath;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitPath;

import lombok.Getter;

public class FolderBean extends PathBean {
	
	@Getter
	private Collection<PathBean> subpath = new ArrayList<PathBean>();
	
	public FolderBean(GitFolder gitFolder, CommitBean commit) {
		super(gitFolder, commit);
		
		for (GitPath path : gitFolder.list()) {
			if (path instanceof GitFolder) {
				subpath.add(new FolderBean((GitFolder)path, commit));
			}
			else {
				assert path instanceof GitFilepath;
				subpath.add(new FilepathBean((GitFilepath)path, commit));
			}
		}
	}
}

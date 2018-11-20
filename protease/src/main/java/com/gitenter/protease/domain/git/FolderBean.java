package com.gitenter.protease.domain.git;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderBean extends PathBean {
	
	private Collection<PathBean> subpath;

	@Override
	public boolean isFolder() {
		return true;
	}

	@Override
	public boolean isFile() {
		return false;
	}
}

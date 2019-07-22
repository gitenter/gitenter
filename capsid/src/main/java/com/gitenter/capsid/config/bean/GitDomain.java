package com.gitenter.capsid.config.bean;

import com.gitenter.protease.config.bean.GitSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitDomain {
	
	private String domainName;
	private Integer port;
	
	public String gitCloneCommand(GitSource gitSource) {
		/*
		 * git clone ssh://git@localhost:8822/home/git/rrr/raa.git
		 */
		return "git clone ssh://git@"+domainName+":"+port+gitSource.getRootDirectory().getAbsolutePath();
	}
}

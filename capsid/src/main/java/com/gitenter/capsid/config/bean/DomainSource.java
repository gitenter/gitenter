package com.gitenter.capsid.config.bean;

import com.gitenter.protease.config.bean.GitSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainSource {
	
	private String domainName;
	private Integer webPort = 80;
	private Integer gitPort = 22;

	public String getGitSshProtocolUrl(GitSource gitSource, String orgName, String repoName) {
		/*
		 * https://git-scm.com/book/en/v2/Git-on-the-Server-The-Protocols#_the_ssh_protocol
		 * ssh://git@localhost:8822/home/git/rrr/raa.git
		 */
		String gitHost = gitPort.equals(22) ? domainName : domainName+":"+gitPort;
		return "ssh://git@"+gitHost+gitSource.getBareRepositoryDirectory(orgName, repoName).getAbsolutePath();
	}
}

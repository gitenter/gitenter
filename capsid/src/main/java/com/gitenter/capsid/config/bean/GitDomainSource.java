package com.gitenter.capsid.config.bean;

import com.gitenter.protease.config.bean.GitSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitDomainSource {
	
	/*
	 * This is not using the value `GitSourceConfig` @Autowired, as 
	 * that's where the web-app container sees the git folder (not the 
	 * git container sees).
	 */
	private GitSource gitSource;
	private String domainName;
	private Integer port = 22;

	public String getGitSshProtocolUrl(String orgName, String repoName) {
		/*
		 * https://git-scm.com/book/en/v2/Git-on-the-Server-The-Protocols#_the_ssh_protocol
		 * ssh://git@localhost:8822/home/git/rrr/raa.git
		 */
		if (port.equals(22)) {
			return "git@"+domainName+":"+gitSource.getBareRepositoryDirectory(orgName, repoName).getAbsolutePath();
		}
		else {
			return "ssh://git@"+domainName+":"+port+gitSource.getBareRepositoryDirectory(orgName, repoName).getAbsolutePath();
		}
	}
}

package com.gitenter.protease.source;

import java.io.File;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import com.gitenter.protease.source.GitSource;

public class GitSourceTest {

	@Test
	public void test1() throws GitAPIException {
		
		System.out.println(GitSource.getBareRepositoryOrganizationName(new File("/path/user1/repo1.git")));
		System.out.println(GitSource.getBareRepositoryName(new File("/path/user1/repo1.git")));
	}

}
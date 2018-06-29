package com.gitenter.protease.source;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import com.gitenter.protease.source.GitSource;

public class GitSourceTest {

	@Test
	public void test1() throws GitAPIException {
		
		File source = new File("/path/org1/repo1.git");
		
		assertEquals(GitSource.getBareRepositoryOrganizationName(source), "org1");
		assertEquals(GitSource.getBareRepositoryName(source), "repo1");
	}

}
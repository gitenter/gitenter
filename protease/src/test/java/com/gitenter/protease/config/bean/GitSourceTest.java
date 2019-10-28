package com.gitenter.protease.config.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;

public class GitSourceTest {

	@Test
	public void test1() throws GitAPIException, IOException {
		
		File source = new File("/path/org1/repo1.git");
		
		assertEquals(GitSource.getBareRepositoryOrganizationName(source), "org1");
		assertEquals(GitSource.getBareRepositoryName(source), "repo1");
	}

}
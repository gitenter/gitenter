package com.gitenter.protease.config.bean;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

import com.gitenter.protease.config.source.GitSourceBean;

public class GitSourceBeanTest {

	@Test
	public void test1() throws GitAPIException, IOException {
		
		File source = new File("/path/org1/repo1.git");
		
		assertEquals(GitSourceBean.getBareRepositoryOrganizationName(source), "org1");
		assertEquals(GitSourceBean.getBareRepositoryName(source), "repo1");
	}

}
package com.gitenter.hook.postreceive.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HookInputSetTest {

	@Test
	public void test() throws IOException {
		
		/* 
		 * TODO:
		 * Should move System.getProperty("user.dir") into HookInputSet? 
		 */
		String userDir = "/home/git/org_name/repo_name.git";
		String args[] = {"oldSha", "newSha", "branchName"};
		
		HookInputSet input = new HookInputSet(userDir, args);
		
		assertEquals(input.getBranchName(), "branchName");
		assertEquals(input.getOldSha(), "oldSha");
		assertEquals(input.getNewSha(), "newSha");
		
		assertEquals(input.getOrganizationName(), "org_name");
		assertEquals(input.getRepositoryName(), "repo_name");
	}
}

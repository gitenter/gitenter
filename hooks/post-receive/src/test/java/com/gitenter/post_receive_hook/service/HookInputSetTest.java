package com.gitenter.post_receive_hook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

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

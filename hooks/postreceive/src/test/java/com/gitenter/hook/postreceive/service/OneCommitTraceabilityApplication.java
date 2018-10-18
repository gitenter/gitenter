package com.gitenter.hook.postreceive.service;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.gitenter.hook.postreceive.service.HookInputSet;
import com.gitenter.hook.postreceive.service.UpdateDatabaseFromGitService;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

/*
 * TODO:
 * This classes should be later on removed completely by the JUnit
 * test classes.
 */
@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.postreceive"})
public class OneCommitTraceabilityApplication {
	
	@Autowired private UpdateDatabaseFromGitService updateDatabase;
	
	public static void main (String[] args) throws Exception {
		
		System.setProperty("spring.profiles.active", "one_commit_traceability");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(OneCommitTraceabilityApplication.class);
		
		OneCommitTraceabilityApplication p = context.getBean(OneCommitTraceabilityApplication.class);
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/one-commit-traceability/org/repo.git");
		File commitRecordFileMaster = new File("/home/beta/Workspace/enterovirus-test/one-commit-traceability/commit-sha-list.txt");
		
		HookInputSet status = new HookInputSet(
				repositoryDirectory,
				new BranchName("master"),
				new CommitSha("0000000000000000000000000000000000000000"),
				new CommitSha(commitRecordFileMaster, 1));
		
		p.run(status);
	}
	
	private void run (HookInputSet status) throws IOException, GitAPIException {
		updateDatabase.update(status);
	}
}

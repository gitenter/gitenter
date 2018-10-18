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

@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.postreceive"})
public class LongCommitPathApplication {
	
	@Autowired private UpdateDatabaseFromGitService updateDatabase;
	
	public static void main (String[] args) throws Exception {
		
		System.setProperty("spring.profiles.active", "long_commit_path");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LongCommitPathApplication.class);
		
		LongCommitPathApplication p = context.getBean(LongCommitPathApplication.class);
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/org/repo.git");
		File commitRecordFileMaster = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/commit-sha-list-master.txt");
		
		HookInputSet status = new HookInputSet(
				repositoryDirectory,
				new BranchName("master"),
				new CommitSha(commitRecordFileMaster, 1),
				new CommitSha(commitRecordFileMaster, 10));
		
		p.run(status);
	}
	
	private void run (HookInputSet status) throws IOException, GitAPIException {
		updateDatabase.update(status);
	}
}

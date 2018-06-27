package com.gitenter.protease.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ResourceUtils;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitNormalBranch;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitRemote;
import com.gitenter.gitar.GitRepository;
import com.gitenter.gitar.GitWorkspace;
import com.gitenter.protease.source.GitSource;

@Configuration
public class TestGitConfig {
//	
//	@Profile("user_auth")
//	@Bean
//	public GitSource userAuthGitSource() {
//		
//		/* 
//		 * Since this test doesn't touch the git part, non-existence doesn't matter. 
//		 */ 
//		GitSource gitSource = new GitSource();
//		gitSource.setRootFolderPath("/dummy/position/not/exist"); 
//		return gitSource;
//	}
//
//	@Profile("one_repo_fix_commit")
//	@Bean
//	public GitSource oneRepoFixCommitGitSource() {
//		
//		GitSource gitSource = new GitSource();
//		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit"));
//		return gitSource;
//	}
//	
//	@Profile("long_commit_path")
//	@Bean
//	public GitSource longCommitPathgitSource() {
//		
//		GitSource gitSource = new GitSource();
//		gitSource.setRootFolderPath(new File(System.getProperty("user.home"), "Workspace/enterovirus-test/long_commit_path/"));
//		return gitSource;
//	}
//	
	
	/*
	 * Minimal includes one single commit on master branch:
	 * 
	 * $ git log
	 * 
	 * commit c36a5aed6e1c9f6a6c59bb21288a9d0bdbe93b73 (HEAD -> master, tag: tag, tag: annotated-tag, origin/master)
	 * Author: Cong-Xin Qiu <ozoox.o@gmail.com>
	 * Date:   Wed Jun 27 07:35:06 2018 -0400
	 *
     * 	commit
	 * (END)
	 */
	@Profile("minimal")
	@Bean
	public GitSource minimalGitSource() throws FileNotFoundException {
		
		GitSource gitSource = mock(GitSource.class);
		when(gitSource.getBareRepositoryDirectory(any(String.class), any(String.class)))
			.thenReturn(ResourceUtils.getFile("classpath:repo/minimal.git"));
		
		return gitSource;
	}

//	@Profile("production")
//	@Bean
//	public GitSource gitSource() throws IOException, GitAPIException {
//		
//		/*
//		 * TODO:
//		 * Optimize it so it execute once per run, rather than once when this
//		 * method is being called.
//		 * 
//		 * TODO:
//		 * Consider move it to a real temporary folder ("TemporaryFolder" class
//		 * can only be used in JUnit test cases).
//		 */
//		File rootDirectory = new File("/tmp/protease");
//		if (rootDirectory.exists()) {
//			FileUtils.forceDelete(rootDirectory);
//		}
//		rootDirectory.mkdirs();
//		File normalDirectory = new File(rootDirectory, "normal-repo");
//		/*
//		 * The "bareDirectory" definition matches the dbunit setup
//		 * of minimal case.
//		 */
//		File bareDirectory = new File(rootDirectory, "organization/repository.git");
//		normalDirectory.mkdir();
//		bareDirectory.mkdir();
//		
//		GitNormalRepository normalRepository = GitNormalRepository.getInstance(normalDirectory);
//		GitBareRepository.getInstance(bareDirectory);
//		
//		normalRepository.createOrUpdateRemote("origin", bareDirectory.toString());
//		GitRemote origin = normalRepository.getRemote("origin");
//		
//		GitWorkspace workspace = normalRepository.getCurrentBranch().checkoutTo();
//		new File(normalDirectory, "file1").createNewFile();
//		workspace.add();
//		workspace.commit("file1");
//		normalRepository.getCurrentBranch().checkoutTo().push(origin);
//		
//		normalRepository.createBranch("another-branch");
//		normalRepository.getBranch("another-branch").checkoutTo().push(origin);
//		
//		workspace = normalRepository.getBranch("master").checkoutTo();
//		new File(normalDirectory, "file2").createNewFile();
//		workspace.add();
//		workspace.commit("file2");
//		normalRepository.getCurrentBranch().checkoutTo().push(origin);
//		/*
//		 * empty
//		 *   |
//		 * file1  <-- another-branch
//		 *   |
//		 * file1&file2  <-- master
//		 */
//		
//		GitSource gitSource = new GitSource();
//		gitSource.setRootFolderPath(rootDirectory);
//		return gitSource;
//	}
}

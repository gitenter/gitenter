package com.gitenter.hook.postreceive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitRemote;
import com.gitenter.gitar.GitWorkspace;
import com.gitenter.hook.postreceive.PostReceiveConfig;
import com.gitenter.protease.dao.auth.RepositoryRepository;
import com.gitenter.protease.dao.git.CommitRepository;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.IgnoredCommitBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=PostReceiveConfig.class)
public class UpdateDatabaseFromGitServiceTest {
	
	@InjectMocks private UpdateDatabaseFromGitService updateDatabaseFromGit;
	
	@Spy private RepositoryRepository repositoryRepository;
	@Mock private CommitRepository commitRepository;
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void testNotSaveIfNoUnsavedBranch() throws IOException, GitAPIException {
		
		String oldSha = GitCommit.EMPTY_SHA;
		String newSha = GitCommit.EMPTY_SHA;
		String branchName = "master";
		
		String userDir = "org_name/repo_name/repo.git";
		String args[] = {oldSha, newSha, branchName};
		
		HookInputSet input = new HookInputSet(userDir, args);
		
		BranchBean mockBranch = mock(BranchBean.class);
		Mockito.when(mockBranch.getUnsavedLog(any(String.class), any(String.class)))
			.thenReturn(new ArrayList<GitCommit>());
		
		RepositoryBean mockRepository = mock(RepositoryBean.class);
		Mockito.when(mockRepository.getBranch(any(String.class))).thenReturn(mockBranch);
		
		Mockito.when(repositoryRepository.findByOrganizationNameAndRepositoryName(any(String.class), any(String.class)))
			.thenReturn(Arrays.asList(mockRepository));
		
		updateDatabaseFromGit.update(input);
		verify(commitRepository, times(0)).saveAndFlush(any(CommitBean.class));
	}
	
	@Test
	public void testValidCommit() throws IOException, GitAPIException {
		
		File normalDirectory = folder.newFolder("valid-repo");
		GitNormalRepository gitNormalRepository = GitNormalRepository.getInstance(normalDirectory);
		GitWorkspace workspace = gitNormalRepository.getCurrentBranch().checkoutTo();
		
		File bareDirectory = folder.newFolder("valid-repo.git");
		GitBareRepository gitBareRepository = GitBareRepository.getInstance(bareDirectory);
		gitNormalRepository.createOrUpdateRemote("origin", gitBareRepository.getDirectory().toString());
		GitRemote origin = gitNormalRepository.getRemote("origin");
		
		String textContent =
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		
		addAFile(normalDirectory, "file.md", textContent);
		
		workspace.add();
		workspace.commit("dummy commit message");
		workspace.push(origin);
		
		GitCommit gitCommit = gitBareRepository.getBranch("master").getHead();
		String oldSha = GitCommit.EMPTY_SHA;
		String newSha = gitCommit.getSha();
		String branchName = "master";
		
		String userDir = bareDirectory.getAbsolutePath();
		String args[] = {oldSha, newSha, branchName};
		
		HookInputSet input = new HookInputSet(userDir, args);
		
		BranchBean mockBranch = mock(BranchBean.class);
		Mockito.when(mockBranch.getUnsavedLog(any(String.class), any(String.class)))
			.thenReturn(Arrays.asList(gitCommit));
		
		RepositoryBean mockRepository = mock(RepositoryBean.class);
		Mockito.when(mockRepository.getBranch(any(String.class))).thenReturn(mockBranch);
		
		Mockito.when(repositoryRepository.findByOrganizationNameAndRepositoryName(any(String.class), any(String.class)))
			.thenReturn(Arrays.asList(mockRepository));
		
		updateDatabaseFromGit.update(input);
		
		ArgumentCaptor<CommitBean> argument = ArgumentCaptor.forClass(CommitBean.class);
		verify(commitRepository, times(1)).saveAndFlush(argument.capture());
		CommitBean savedCommit = argument.getValue();
		
		assertEquals(savedCommit.getRepository(), mockRepository);
		
		/*
		 * TODO:
		 * 
		 * Modify the test so the "ValidCommitBean" case will be tested.
		 * 
		 * And/or we may have multiple commits in `getUnsavedLog()` then
		 * we can test it is saved multiple times.
		 */
		assertTrue(savedCommit instanceof IgnoredCommitBean);
	}

	private void addAFile(File directory, String filename, String textContent) throws IOException {
		
		File file = new File(directory, filename);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(textContent);
		writer.close();
	}
}

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
import java.util.Random;

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
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=PostReceiveConfig.class)
public class UpdateDatabaseFromGitServiceTest {
	
	@InjectMocks private UpdateDatabaseFromGitServiceImpl service;
	
	@Spy private RepositoryRepository repositoryRepository;
	@Mock private CommitRepository commitRepository;
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	private GitNormalRepository gitNormalRepository;
	private GitBareRepository gitBareRepository;
	
	private GitWorkspace workspace;
	private GitRemote origin;
	
	@Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
	
	private void setupGit() throws IOException, GitAPIException {
		
		Random rand = new Random();
		String name = "repo-"+rand.nextInt(Integer.MAX_VALUE);
		File normalDirectory = folder.newFolder(name);
		gitNormalRepository = GitNormalRepository.getInstance(normalDirectory);
		workspace = gitNormalRepository.getCurrentBranch().checkoutTo();
		
		File bareDirectory = folder.newFolder(name+".git");
		gitBareRepository = GitBareRepository.getInstance(bareDirectory);
		gitNormalRepository.createOrUpdateRemote("origin", gitBareRepository.getDirectory().toString());
		origin = gitNormalRepository.getRemote("origin");
	}
	
	private HookInputSet getHookInputSet() throws IOException, GitAPIException {
		
		GitCommit headGitCommit = gitBareRepository.getBranch("master").getHead();
		String oldSha = GitCommit.EMPTY_SHA;
		String newSha = headGitCommit.getSha();
		String branchName = "master";
		
		String userDir = gitBareRepository.getDirectory().getAbsolutePath();
		String args[] = {oldSha, newSha, branchName};
		
		HookInputSet input = new HookInputSet(userDir, args);
		
		return input;
	}
	
	/*
	 * Probably can merge this method to the previous one, with the input as `List<GitCommit>`
	 * and for this case give empty string. But it is troublesome because in this case we can completely
	 * forget git temporary file setup (probably we shouldn't, as that hacks the system).
	 * 
	 * Will re-think about it later.
	 */
	private HookInputSet getDummyHookInputSet() {
		
		String oldSha = GitCommit.EMPTY_SHA;
		String newSha = GitCommit.EMPTY_SHA;
		String branchName = "master";
		
		String userDir = "org_name/repo_name/repo.git";
		String args[] = {oldSha, newSha, branchName};
		
		HookInputSet input = new HookInputSet(userDir, args);
		
		return input;
	}
	
	private List<RepositoryBean> spyRepositoryRepositoryAndGetMockRepositories(HookInputSet input) throws IOException, GitAPIException {
		
		List<GitCommit> gitCommits;
		if (input.getNewSha().equals(GitCommit.EMPTY_SHA)) {
			gitCommits = new ArrayList<GitCommit>();
		}
		else {
			gitCommits = gitBareRepository.getBranch("master").getLog(input.getOldSha(), input.getNewSha());
		}
		BranchBean mockBranch = mock(BranchBean.class);
		Mockito.when(mockBranch.getUnsavedLog(any(String.class), any(String.class)))
			.thenReturn(gitCommits);
		
		RepositoryBean mockRepository = mock(RepositoryBean.class);
		Mockito.when(mockRepository.getBranch(any(String.class))).thenReturn(mockBranch);
		
		List<RepositoryBean> mockRepositories = Arrays.asList(mockRepository);
		Mockito.when(repositoryRepository.findByOrganizationNameAndRepositoryName(any(String.class), any(String.class)))
			.thenReturn(mockRepositories);
		
		return mockRepositories;
	}
	
	private void addAFile(File directory, String filename, String textContent) throws IOException {
		
		File file = new File(directory, filename);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(textContent);
		writer.close();
	}
	
	@Test
	public void testValidCommit() throws IOException, GitAPIException {
		
		setupGit();
		
		String fileTextContent =
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		addAFile(gitNormalRepository.getDirectory(), "file.md", fileTextContent);
		
		String configTextContent = "enable_systemwide = on";
		addAFile(gitNormalRepository.getDirectory(), "gitenter.properties", configTextContent);
		
		workspace.add();
		workspace.commit("valid commit");
		workspace.push(origin);
		
		HookInputSet input = getHookInputSet();		
		List<RepositoryBean> mockRepositories = spyRepositoryRepositoryAndGetMockRepositories(input);
		service.update(input);
		
		ArgumentCaptor<CommitBean> argument = ArgumentCaptor.forClass(CommitBean.class);
		verify(commitRepository, times(1)).saveAndFlush(argument.capture());
		CommitBean savedCommit = argument.getValue();
		
		assertEquals(savedCommit.getRepository(), mockRepositories.get(0));
		assertTrue(savedCommit instanceof ValidCommitBean);
	}
	
	@Test
	public void testInvalidCommit() throws IOException, GitAPIException {
		
		setupGit();
		
		String fileTextContent = "- [tag]{refer-not-exist} a traceable item.";
		addAFile(gitNormalRepository.getDirectory(), "file.md", fileTextContent);
		
		String configTextContent = "enable_systemwide = on";
		addAFile(gitNormalRepository.getDirectory(), "gitenter.properties", configTextContent);
		
		workspace.add();
		workspace.commit("invalid commit");
		workspace.push(origin);
		
		HookInputSet input = getHookInputSet();		
		List<RepositoryBean> mockRepositories = spyRepositoryRepositoryAndGetMockRepositories(input);
		service.update(input);
		
		ArgumentCaptor<CommitBean> argument = ArgumentCaptor.forClass(CommitBean.class);
		verify(commitRepository, times(1)).saveAndFlush(argument.capture());
		CommitBean savedCommit = argument.getValue();
		
		assertEquals(savedCommit.getRepository(), mockRepositories.get(0));
		assertTrue(savedCommit instanceof InvalidCommitBean);
	}
	
	@Test
	public void testIgnoredCommit() throws IOException, GitAPIException {
		
		setupGit();
		
		String textContent = "enable_systemwide = off";
		addAFile(gitNormalRepository.getDirectory(), "gitenter.properties", textContent);
		
		workspace.add();
		workspace.commit("setup as disabled");
		workspace.push(origin);
		
		HookInputSet input = getHookInputSet();		
		List<RepositoryBean> mockRepositories = spyRepositoryRepositoryAndGetMockRepositories(input);
		service.update(input);
		
		ArgumentCaptor<CommitBean> argument = ArgumentCaptor.forClass(CommitBean.class);
		verify(commitRepository, times(1)).saveAndFlush(argument.capture());
		CommitBean savedCommit = argument.getValue();
		
		assertEquals(savedCommit.getRepository(), mockRepositories.get(0));
		assertTrue(savedCommit instanceof IgnoredCommitBean);
	}
	
	@Test
	public void testNotSaveIfNoUnsavedBranch() throws IOException, GitAPIException {
		
		HookInputSet input = getDummyHookInputSet();		
		spyRepositoryRepositoryAndGetMockRepositories(input);
		service.update(input);
		
		service.update(getDummyHookInputSet());
		verify(commitRepository, times(0)).saveAndFlush(any(CommitBean.class));
	}
	
	@Test
	public void testMultipleCommitsWillBeSavedMultipleTinmes() throws IOException, GitAPIException {
		
		setupGit();
		
		String configTextContent = "enable_systemwide = on";
		addAFile(gitNormalRepository.getDirectory(), "gitenter.properties", configTextContent);
		
		workspace.add();
		workspace.commit("first (valid) commit");
		workspace.push(origin);
		
		String fileTextContent = "- [tag]{refer-not-exist} a traceable item.";
		addAFile(gitNormalRepository.getDirectory(), "file.md", fileTextContent);
		
		workspace.add();
		workspace.commit("second (invalid) commit");
		workspace.push(origin);
		
		HookInputSet input = getHookInputSet();		
		List<RepositoryBean> mockRepositories = spyRepositoryRepositoryAndGetMockRepositories(input);
		service.update(input);
		
		ArgumentCaptor<CommitBean> argument = ArgumentCaptor.forClass(CommitBean.class);
		verify(commitRepository, times(2)).saveAndFlush(argument.capture());
		List<CommitBean> savedCommits = argument.getAllValues();
		
		/*
		 * Second commit will be saved first, while the first commit will be saved later.
		 * That is reverse time order.
		 */
		assertEquals(savedCommits.get(1).getRepository(), mockRepositories.get(0));
		assertTrue(savedCommits.get(1) instanceof ValidCommitBean);
		
		assertEquals(savedCommits.get(0).getRepository(), mockRepositories.get(0));
		assertTrue(savedCommits.get(0) instanceof InvalidCommitBean);
	}
}

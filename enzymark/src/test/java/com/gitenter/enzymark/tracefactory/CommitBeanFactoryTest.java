package com.gitenter.enzymark.tracefactory;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.InvalidCommitBean;
import com.gitenter.protease.domain.git.ValidCommitBean;

public class CommitBeanFactoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testGetValidCommit() throws IOException, GitAPIException {
		
		String textContent1 = 
				  "- [tag1] a traceable item.\n"
				+ "- [tag2]{tag1} a traceable item with in-document reference.";
		
		String textContent2 = "- [tag3]{tag1,tag2} a traceable item with cross-document reference.";
		
		File directory = folder.newFolder("repo");
		GitNormalRepository repository = GitNormalRepository.getInstance(directory);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		addAFile(directory, "file1.md", textContent1);
		
		File subfolder = new File(directory, "folder");
		subfolder.mkdir();
		addAFile(subfolder, "file2.md", textContent2);
		
		workspace.add();
		workspace.commit("dummy commit message");
		GitCommit gitCommit = repository.getCurrentBranch().getHead();
		
		CommitBeanFactory factory = new CommitBeanFactory();
		CommitBean commit = factory.getCommit(gitCommit);
		
		assert commit instanceof ValidCommitBean;
//		ValidCommitBean validCommit = (ValidCommitBean)commit;
		
		/*
		 * TODO:
		 * 
		 * It is really hard to assert anything in here, because:
		 * 
		 * (1) `getDocuments()` is private for `validCommit`, while although there are public
		 * `getRoot()` and `getFile()`, they need to be initialized throught `CommitRepositoryImpl`
		 * which has private classes involved.
		 * 
		 * (2) The other possibility is to save to the database and query back, then all the
		 * database/git part of the domain class will be setup properly. However, by doing so 
		 * we need to turn on dbunit in this package, which is kind of too much.
		 */
	}
	
	@Test
	public void testGetInvalidCommit() throws IOException, GitAPIException {
		
		String textContent = "- [tag]{refer-not-exist} a traceable item.";
		
		File directory = folder.newFolder("repo");
		GitNormalRepository repository = GitNormalRepository.getInstance(directory);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		addAFile(directory, "file.md", textContent);
		
		workspace.add();
		workspace.commit("dummy commit message");
		GitCommit gitCommit = repository.getCurrentBranch().getHead();
		
		CommitBeanFactory factory = new CommitBeanFactory();
		CommitBean commit = factory.getCommit(gitCommit);
		
		assert commit instanceof InvalidCommitBean;
		InvalidCommitBean invalidCommit = (InvalidCommitBean)commit;
		
		assertTrue(invalidCommit.getErrorMessage().contains("is not existed throughout the system"));
	}
	
	private void addAFile(File directory, String filename, String textContent) throws IOException {
		
		File file = new File(directory, filename);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(textContent);
		writer.close();
	}
}

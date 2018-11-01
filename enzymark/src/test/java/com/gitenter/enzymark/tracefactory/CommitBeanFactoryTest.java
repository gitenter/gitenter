package com.gitenter.enzymark.tracefactory;

import static org.junit.Assert.assertEquals;
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
import com.gitenter.protease.domain.git.DocumentBean;
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
		ValidCommitBean validCommit = (ValidCommitBean)commit;
		
		assertEquals(validCommit.getDocuments().size(), 2);
		for (DocumentBean document : validCommit.getDocuments()) {
			
			/*
			 * Can't use `validCommit.getDocument("file1.md")` because that will trigger
			 * `getFile` which needs the placeholders which are not properly setup
			 * unless we get it through the database.
			 */
			if (document.getRelativePath().equals("file1.md")) {
				assertEquals(document.getTraceableItems().size(), 2);
			}
			
			if (document.getRelativePath().equals("file2.md")) {
				assertEquals(document.getTraceableItems().size(), 1);
			}
		}
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

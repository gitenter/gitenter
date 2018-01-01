package enterovirus.protease.database;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "one_repo_fix_commit")
@ContextConfiguration(classes=ComponentScanConfig.class)
public class DocumentRepositoryTest {

	@Autowired private DocumentRepository repository;

	private void showDocumentBean (DocumentBean document) {
		System.out.println("This document is a: "+document.getClass());
		System.out.println("Organization: "+document.getCommit().getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+document.getCommit().getRepository().getName());
		System.out.println("Commit SHA: "+document.getCommit().getShaChecksumHash());
		
		System.out.println("Relative Filepath: "+document.getRelativeFilepath());
		System.out.println("Content: ");
		System.out.println(document.getLineContents());
		for (LineContentBean content : document.getLineContents()) {
			System.out.println(content.getContent());
		}
	}
	
	@Test
	@Transactional
	public void testUnmodifiedFindById() throws Exception {
		DocumentBean document = repository.findById(1);
		assertThat(document, instanceOf(DocumentModifiedBean.class));
		showDocumentBean(document);
	}
	
	@Test
	@Transactional
	public void testModifiedFindById() throws Exception {
		DocumentBean document = repository.findById(4);
		assertThat(document, instanceOf(DocumentUnmodifiedBean.class));
		showDocumentBean(document);
	}
	
	@Test
	@Transactional
	public void testFindByCommitIdAndRelativeFilepath() throws Exception {
		/*
		 * Need to test several different conditions:
		 * Unmodified/modified
		 * Exist/non-exist other documents with the same filepath
		 * TODO:
		 * write a better test case later. 
		 */
		DocumentBean document = repository.findByCommitIdAndRelativeFilepath(2, "1st-commit-folder/2nd-commit-file-under-1st-commit-folder");
//		DocumentBean document = repository.findByCommitIdAndRelativeFilepath(6, "folder_1/same-name-file");
//		DocumentBean document = repository.findByCommitIdAndRelativeFilepath(7, "test-add-a-file-from-client_1");
		showDocumentBean(document);
	}
	
	@Test
	@Transactional
	public void testFindByRepositoryIdAndRelativeFilepath() throws Exception {
		DocumentBean document = repository.findByRepositoryIdAndRelativeFilepath(1, "1st-commit-folder/2nd-commit-file-under-1st-commit-folder");
		showDocumentBean(document);
	}
}

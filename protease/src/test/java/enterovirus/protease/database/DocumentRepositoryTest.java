package enterovirus.protease.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import enterovirus.protease.ProteaseConfig;
import enterovirus.protease.domain.DocumentBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "production")
@ContextConfiguration(classes={ProteaseConfig.class})
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={
		"schemaSettingsDatabaseConnection",
		"schemaGitDatabaseConnection"})
public class DocumentRepositoryTest {

	@Autowired private DocumentRepository repository;

	private void showDocumentBean (DocumentBean document) {
		System.out.println("This document is a: "+document.getClass());
		System.out.println("Organization: "+document.getCommit().getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+document.getCommit().getRepository().getName());
		System.out.println("Commit SHA: "+document.getCommit().getShaChecksumHash());
		
		System.out.println("Relative Filepath: "+document.getRelativeFilepath());
		System.out.println("Content: ");
		System.out.println(document.getContent());
	}
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaSettingsDatabaseConnection", value="dbunit-data/minimal-schema-settings.xml")
	@DatabaseSetup(connection="schemaGitDatabaseConnection", value="dbunit-data/minimal-schema-git.xml")
	public void testFindById() throws Exception {
//		DocumentBean document = repository.findById(1);
//		
//		showDocumentBean(document);
//
//		for (DocumentBean.LineContent content : document.getLineContents()) {
//			System.out.println(content.getContent());
//		}
	}
	
//	@Test
//	@Transactional
//	public void testFindByCommitIdAndRelativeFilepath() throws Exception {
//		/*
//		 * Need to test several different conditions:
//		 * Exist/non-exist other documents with the same filepath
//		 * TODO:
//		 * write a better test case later. 
//		 */
//		DocumentBean document = repository.findByCommitIdAndRelativeFilepath(2, "1st-commit-folder/2nd-commit-file-under-1st-commit-folder");
////		DocumentBean document = repository.findByCommitIdAndRelativeFilepath(6, "folder_1/same-name-file");
////		DocumentBean document = repository.findByCommitIdAndRelativeFilepath(7, "test-add-a-file-from-client_1");
//		showDocumentBean(document);
//	}
//	
//	@Test
//	@Transactional
//	public void testFindByCommitIdAndRelativeFilepathIn() throws Exception {
//		
//		List<String> filepaths = new ArrayList<String>();
//		filepaths.add("1st-commit-folder/1st-commit-file-under-1st-commit-folder");
//		filepaths.add("1st-commit-file-under-root");
//		
//		List<DocumentBean> documents = repository.findByCommitIdAndRelativeFilepathIn(1, filepaths);
//		
//		for (DocumentBean document : documents) {
//			showDocumentBean(document);
//		}
//	}
//
//	@Test
//	@Transactional
//	public void testFindByCommitShaAndRelativeFilepath() throws Exception {
//		
//		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/commit-sha-list.txt");
//		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
//		String filepath = "1st-commit-folder/1st-commit-file-under-1st-commit-folder";
//		
//		DocumentBean document = repository.findByCommitShaAndRelativeFilepath(commitSha, filepath);
//		
//		assertEquals(document.getRelativeFilepath(), filepath);
//	}
//	
//	@Test
//	@Transactional
//	public void testFindByRepositoryIdAndBranchAndRelativeFilepath() throws Exception {
//		
//		String branch = "master";
//		String filepath = "1st-commit-folder/2nd-commit-file-under-1st-commit-folder";
//		
//		DocumentBean document = repository.findByRepositoryIdAndBranchAndRelativeFilepath(1, new BranchName(branch), filepath);
//		
//		assertEquals(document.getRelativeFilepath(), filepath);
//	}
}

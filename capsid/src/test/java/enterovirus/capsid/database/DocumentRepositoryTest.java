package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.capsid.domain.*;

/*
 * @DataJpaTest cannot @Autowired the DataSource, so I use 
 * annotation @SpringBootTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentRepositoryTest {

	@Autowired private DocumentRepository repository;

	private void showDocumentBean (DocumentBean document) {
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
	public void test1() throws Exception {
		DocumentBean document = repository.findById(1);
		showDocumentBean(document);
	}
	
	@Test
	@Transactional
	public void test2() throws Exception {
		DocumentBean document = repository.findByCommitIdAndRelativeFilepath(6, "folder_1/same-name-file");
		showDocumentBean(document);
	}
	
	@Test
	@Transactional
	public void test3() throws Exception {
		DocumentBean document = repository.findByRepositoryIdAndRelativeFilepath(1, "folder_1/same-name-file");
		showDocumentBean(document);
	}
}

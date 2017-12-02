package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.hibernate.Hibernate;
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

	@Autowired
	private DocumentRepository repository;
	
	@Test
	@Transactional
	public void find() throws Exception {
		DocumentBean document = repository.findById(1).get(0);
		System.out.println("Organization: "+document.getCommit().getRepository().getOrganization().getName());
		System.out.println("Repository Name: "+document.getCommit().getRepository().getName());
		System.out.println("Commit SHA: "+document.getCommit().getShaChecksumHash());
		System.out.println("Filepath: "+document.getFilepath());
		System.out.println("Content: ");
		System.out.println(document.getLineContents());
		for (LineContentBean content : document.getLineContents()) {
			System.out.println(content.getContent());
		}
	}
}

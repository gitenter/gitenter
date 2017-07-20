package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import enterovirus.capsid.domain.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentRepositoryTest {

	@Autowired
	private DocumentRepository repository;
	
	@Test
	public void test() throws Exception {

		DocumentBean document = repository.findDocument("user1", "repo1", "master", "folder_1/same-name-file");
		System.out.println(document.getLineContents());
	}
}

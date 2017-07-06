package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import enterovirus.capsid.domain.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TextFileRepositoryTest {

	@Autowired
	private TextFileRepository repository;
	
	@Test
	public void test() throws Exception {

		TextFileBean textFile = repository.findTextFile("user1", "repo1", "master", "folder_1/same-name-file");
		System.out.println(new String(textFile.getContent()));
	}
}

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

		TextFileBean blob = repository.findTextFile();
		System.out.println(new String(blob.getContent()));
	}
}

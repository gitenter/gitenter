package enterovirus.capsid.database;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import enterovirus.capsid.domain.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlobRepositoryTest {

	@Autowired
	private BlobRepository repository;
	
	@Test
	public void test() throws Exception {

		BlobBean blob = repository.findBlob();
		System.out.println(new String(blob.getBlobContent()));
	}
}

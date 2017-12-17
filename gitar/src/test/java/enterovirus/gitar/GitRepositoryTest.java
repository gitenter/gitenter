package enterovirus.gitar;

import java.io.File;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitRepositoryTest {

	@Test
	public void test1() throws GitAPIException {
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/local/junit-test.git");
		GitRepository.initBare(repositoryDirectory);
	}

}
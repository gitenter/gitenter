package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitRepositoryTest {

	@Test
	public void test1() throws GitAPIException, IOException {
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus_data/local/junit-test.git");
		File sampleHooksDirectory = new File("/home/beta/Workspace/enterovirus_data/local/sample-hooks");
		GitRepository.initBare(repositoryDirectory, sampleHooksDirectory);
	}

}
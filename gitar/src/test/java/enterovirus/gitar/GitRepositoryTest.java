package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitRepositoryTest {

	@Test
	public void test1() throws GitAPIException, IOException {
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/git-init/tmp-for-git-init-with-sample-hook.git");
		File sampleHooksDirectory = new File("/home/beta/Workspace/enterovirus-test/git-init/sample-hooks");
		GitRepository.initBare(repositoryDirectory, sampleHooksDirectory);
	}

}
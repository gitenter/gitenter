package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitRepositoryTest {

	@Test
	public void test1() throws GitAPIException, IOException {
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/git_init/tmp-for-git-init-with-sample-hook.git");
		File sampleHooksDirectory = new File("/home/beta/Workspace/enterovirus/capsid/src/main/resources/git-server-side-hooks");

		FileUtils.deleteDirectory(repositoryDirectory);
		GitRepository.initBare(repositoryDirectory, sampleHooksDirectory);
	}
	
	@Test
	public void test2() throws GitAPIException, IOException {
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/git_init/tmp-for-git-init-with-sample-hook-and-config-files.git");
		File sampleHooksDirectory = new File("/home/beta/Workspace/enterovirus/capsid/src/main/resources/git-server-side-hooks");		
		File configFilesDirectory = new File("/home/beta/Workspace/enterovirus/capsid/src/main/resources/config-files");
		
		FileUtils.deleteDirectory(repositoryDirectory);
		GitRepository.initBareWithConfig(repositoryDirectory, sampleHooksDirectory, configFilesDirectory);
	}
}
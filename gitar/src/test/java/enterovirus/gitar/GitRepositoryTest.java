package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testBuild() throws IOException {
		
		File directory = folder.newFolder("repo");
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository jGitRepository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
		
		// print nothing
		for (File file : directory.listFiles()) {
			System.out.println(file);
		}
		
		// JGit return isBare() true value even if the folder is empty.
		// Bug reported: https://bugs.eclipse.org/bugs/show_bug.cgi?id=535333
		assertTrue(jGitRepository.isBare());
	}
}

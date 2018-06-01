package enterovirus.gitar.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JGitTest {

	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testAddAndCommit() throws IOException, IllegalStateException, GitAPIException {

		File directory = folder.newFolder("repo");
		Git.init().setDirectory(directory).setBare(false).call();

		new File(directory, "a-file").createNewFile();
		
		/*
		 * Can't use repository build by "FileRepositoryBuilder".
		 * That's for a bare repository.
		 */
		try (Git git = Git.open(directory)) {
			git.add().addFilepattern(".").call();
		}
	}

	@Test
	public void testBuilder() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
		
		assertTrue(repository.isBare());
	}
	
	@Test
	public void testJGitRepository()  throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		Git.init().setDirectory(directory).setBare(false).call();
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
		
		System.out.println("alalala"+repository.getBranch());
		
		new File(directory, "a-file").createNewFile();
		
		try (Git git = Git.open(directory)) {
			git.add().addFilepattern(".").call();
			git.commit().setMessage("a message").call();
			
			System.out.println(git.branchList().call().size());
			git.checkout().setName("master").call();
		}
		
		System.out.println("alaaalala"+repository.getBranch()); // should return some reasonable thing, but actually return null.
	}
}
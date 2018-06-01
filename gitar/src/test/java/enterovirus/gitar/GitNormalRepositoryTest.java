package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitNormalRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	static GitNormalRepository getOne(TemporaryFolder folder) throws IOException, GitAPIException {
		
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		File directory = folder.newFolder(name);
		return new GitNormalRepository(directory);
	}

	@Test
	public void testInit() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		new GitNormalRepository(directory);
		
		assertTrue(new File(directory, ".git").isDirectory());
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		new GitNormalRepository(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderReadOnly() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		directory.setReadOnly();
		
		new GitBareRepository(directory);
	}
	
	@Test(expected = IOException.class)
	public void testDirectoryInitToOtherType() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		
		new GitBareRepository(directory);
		new GitNormalRepository(directory);
	}
	
	@Test
	public void testRemote() throws IOException, GitAPIException {
	
		GitNormalRepository repository = getOne(folder);
		
		repository.addRemote("origin", "/fake/url");
		assertEquals(repository.getRemote("origin").url, "/fake/url");
	}
	
	@Test
	public void testBranch() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		GitNormalRepository repository = new GitNormalRepository(directory);
		
		assertFalse(repository.getBranch("branch-not-exist").exist());
		
		/* 
		 * This behavior is actually the same as git (although some editors
		 * like atom cheats). 
		 * 
		 * No branch exists when there is no commit. After the first commit
		 * "master" branch appears. Also, one cannot create a new branch
		 * if no commit exists (so the bug in https://bugs.eclipse.org/bugs/show_bug.cgi?id=349667
		 * is actually actually not correct).
		 * 
		 * After the first commit, "master" branch appears, and user can
		 * also create a new branch.
		 */
		assertFalse(repository.getBranch("master").exist());
		
		try {
			repository.createBranch("new-branch");
		}
		catch (RefNotFoundException e) {
			; //ignore
		}
	}
}

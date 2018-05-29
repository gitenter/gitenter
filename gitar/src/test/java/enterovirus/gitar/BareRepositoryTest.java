package enterovirus.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BareRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testInit() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("bare-repo");
		
		new BareRepository(directory);
		
		assertTrue(new File(directory, "branches").isDirectory());
		assertTrue(new File(directory, "hooks").isDirectory());
		assertTrue(new File(directory, "logs").isDirectory());
		assertTrue(new File(directory, "objects").isDirectory());
		assertTrue(new File(directory, "refs").isDirectory());
		assertTrue(new File(directory, "config").isFile());
		assertTrue(new File(directory, "HEAD").isFile());
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		
		new BareRepository(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderReadOnly() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("bare-repo");
		directory.setReadOnly();
		
		new BareRepository(directory);
	}
}

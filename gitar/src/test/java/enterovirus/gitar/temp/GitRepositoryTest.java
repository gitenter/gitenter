package enterovirus.gitar.temp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import enterovirus.gitar.temp.GitRepository;

import static org.junit.Assert.*;

public class GitRepositoryTest {
	
	@Rule public TemporaryFolder folder= new TemporaryFolder();
	
	private File hooksDirectory;
	private File configDirectory;
	
	@Before
	public void initialize() throws IOException {
		
		hooksDirectory = folder.newFolder("hook");
		new File(hooksDirectory, "this-is-a-hook").createNewFile();
		
		configDirectory = folder.newFolder("config");
		new File(configDirectory, "this-is-a-config").createNewFile();
	}

	@Test
	public void testInitBare() throws GitAPIException, IOException {
		
		File repositoryDirectory = folder.newFolder("bare-repo");
		
		GitRepository.initBare(repositoryDirectory, hooksDirectory);
		
		File hook = new File(repositoryDirectory, "hooks/this-is-a-hook");
		assertTrue(hook.exists());
		assertTrue(hook.isFile());
	}
	
	@Test
	public void testInitBareWithConfig() throws GitAPIException, IOException {
		
		File repositoryDirectory = folder.newFolder("bare-repo-with-config");
		
		GitRepository.initBareWithConfig(repositoryDirectory, hooksDirectory, configDirectory);
		
		File hook = new File(repositoryDirectory, "hooks/this-is-a-hook");
		assertTrue(hook.exists());
		assertTrue(hook.isFile());
		
		/*
		 * TODO:
		 * Check there is indeed one commit, and the config file is written in. But
		 * This is not quite trivial, since it is a bare repo without the files explicitly shown.
		 */
	}
}
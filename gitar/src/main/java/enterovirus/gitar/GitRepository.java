package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

public class GitRepository {
	
	static Repository getRepositoryFromDirectory(File repositoryDirectory) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		return repository;
	}
	
	static public void initBare (File repositoryDirectory, File sampleHooksDirectory) throws GitAPIException, IOException {
		
		Git.init().setDirectory(repositoryDirectory).setBare(true).call();
		
		setupHooks(repositoryDirectory, sampleHooksDirectory);
	}
	
	static public void initBareWithConfig (File repositoryDirectory, File sampleHooksDirectory, File configFilesDirectory) throws GitAPIException, IOException {
		
		Git.init().setDirectory(repositoryDirectory).setBare(true).call();
		
		/*
		 * TODO:
		 * Seems cannot successfully delete the file yet.
		 */
		File localRepositoryDirectory = Files.createTempDirectory("git-repo-").toFile();
		localRepositoryDirectory.deleteOnExit();
		
		try (Git git = Git.cloneRepository()
				.setURI(repositoryDirectory.getAbsolutePath())
				.setDirectory(localRepositoryDirectory).call()) {
		
			FileUtils.copyDirectory(configFilesDirectory, localRepositoryDirectory);
			git.add().addFilepattern(".").call();
			git.commit().setMessage("Initialization with system setup files.").call();
			
			String branch = "master";
			RefSpec spec = new RefSpec(branch + ":" + branch);
			git.push().setRemote(repositoryDirectory.getAbsolutePath()).setRefSpecs(spec).call();
			/*
			 * TODO:
			 * 
			 * First commit need to go BEFORE setup hooks, because otherwise "git push"
			 * will trigger the post-receive hook, but the repository row doesn't exist
			 * in SQL yet. The hook then raise error (I haven't see the error yet, because
			 * it is neither in capsid console or terminal console).
			 * 
			 * However, in this way we can only write to the database manually.
			 * That need to use protease, but gitar doesn't depend on protease. So currently
			 * we can only do it through AdminController rather than GitRepository.
			 * 
			 * It is really dirty. Consider a better way to handle that.
			 */
		}
		
		setupHooks(repositoryDirectory, sampleHooksDirectory);
	}
	
	static private void setupHooks (File repositoryDirectory, File sampleHooksDirectory) throws IOException {
		
		/*
		 * Copy Git server-side hooks to the desired directory.
		 * 
		 * TODO:
		 * Rather than manually make the hooks, and do it in git,
		 * consider doing hook inside of JGit.
		 * May refer to:
		 * (1) Using Git hooks together with JGit / Egit: https://zauner.nllk.net/post/0001-git-hooks-currently-supported-by-jgit/
		 * (2) jgit does not support the same hooks that 'man githooks' talks about: http://gitolite.com/gitolite/overview/
		 * 
		 * TODO:
		 * What about using symlink rather than physically copy
		 * the inside materials?
		 */
		File hookDirectory = new File(repositoryDirectory, "hooks");
		FileUtils.copyDirectory(sampleHooksDirectory, hookDirectory);
		
		/*
		 * See the following link for a list of possible server side hooks:
		 * https://git-scm.com/docs/githooks
		 * 
		 * In here, I just set them all. If some is not needed, we can just
		 * write blank in the corresponding hook.
		 */
		new File(hookDirectory, "pre-receive").setExecutable(true);
		new File(hookDirectory, "update").setExecutable(true);
		new File(hookDirectory, "post-receive").setExecutable(true);
		new File(hookDirectory, "post-update").setExecutable(true);
	}
}

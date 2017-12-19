package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitRepository {
	
	static public void initBare (File repositoryDirectory, File sampleHooksDirectory) throws GitAPIException, IOException {
		Git.init().setDirectory(repositoryDirectory).setBare(true).call();
		
		FileUtils.copyDirectory(sampleHooksDirectory, new File(repositoryDirectory, "hooks"));
	}
}

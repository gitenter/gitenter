package enterovirus.gitar;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitRepository {
	
	static public void initBare (File repositoryDirectory) throws GitAPIException {
		Git.init().setDirectory(repositoryDirectory).setBare(true).call();
	}
}

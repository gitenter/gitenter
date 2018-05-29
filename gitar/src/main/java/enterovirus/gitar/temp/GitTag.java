package enterovirus.gitar.temp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import enterovirus.gitar.wrap.*;

public class GitTag {

	private List<TagName> tagNames = new ArrayList<TagName>();
	
	public GitTag(File repositoryDirectory) throws IOException, GitAPIException {
		
		Repository repository = GitBareRepository.getRepositoryFromDirectory(repositoryDirectory);
		try (Git git = new Git(repository)) {
			List<Ref> call = git.tagList().call();
			for (Ref ref : call) {
				/*
				 * Parse "refs/tags/merged" and get the name "master".
				 * So for other branches.
				 */
				tagNames.add(new TagName(ref.getName().split("/")[2]));
			}
		}
	}

	public List<TagName> getTagNames() {
		return tagNames;
	}
}

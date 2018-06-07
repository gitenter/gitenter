package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitTag extends GitCommit {

	protected final String name;
	
	public String getName() {
		return name;
	}

	GitTag(GitRepository repository, String name) throws IOException {
		
		super(repository, repository.getJGitRepository().exactRef("refs/tags/"+name).getObjectId().getName());
		this.name = name;
	}
	
	GitTag downCasting() throws IOException {
		
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			RevTag jGitTag = revWalk.parseTag(getObjectId());
			return new GitAnnotatedTag(this, jGitTag);
		}
		catch(IncorrectObjectTypeException notAnAnnotatedTag) {
			return new GitLightweightTag(this);
		}
	}
}

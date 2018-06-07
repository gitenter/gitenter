package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitTag {

	protected final String name;
	protected final GitRepository repository;
	
	/*
	 * TODO:
	 * 
	 * As the structure are really similar, should GitTag be actually
	 * a subclass of GitCommit?
	 */
	Ref ref;
	
	public String getName() {
		return name;
	}

	GitTag(GitRepository repository, String name) throws IOException {
		this.repository = repository;
		this.name = name;
		
		ref = repository.getJGitRepository().exactRef("refs/tags/"+name);
	}
	
	GitTag downcasting() throws IOException {
		
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			RevTag jGitTag = revWalk.parseTag(ref.getObjectId());
			return new GitAnnotatedTag(this, jGitTag);
		}
		catch(IncorrectObjectTypeException notAnAnnotatedTag) {
			return new GitLightweightTag(this);
		}
	}
	
	/*
	 * TODO:
	 * This method is really similar to GitBranch.getHead() .
	 * Consider to setup some structure later.
	 */
	public GitCommit getCommit() throws IOException {
		return new GitCommit(repository, ref.getObjectId().getName());
	}
}

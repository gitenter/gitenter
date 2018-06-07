package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevTag;

public class GitAnnotatedTag extends GitTag {
	
	private final String message;
	
	final RevTag jGitTag;

	public String getMessage() {
		return message;
	}

	GitAnnotatedTag(GitTag tag, RevTag jGitTag) throws IOException {
		super(tag.repository, tag.name);
		this.jGitTag = jGitTag;
		this.message = jGitTag.getFullMessage();
	}

}

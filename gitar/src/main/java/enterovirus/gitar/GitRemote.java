package enterovirus.gitar;

public class GitRemote {

	private final String name;
	final String url;

	final GitRepository repository;
	
	public GitRemote(GitRepository repository, String name, String url) {
		this.repository = repository;
		this.name = name;
		this.url = url;
	}
	
	
}

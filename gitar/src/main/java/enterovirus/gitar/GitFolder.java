package enterovirus.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GitFolder extends GitPath {
	
	private Map<String,GitPath> subpathMap = new HashMap<String,GitPath>();
	
	protected void addSubpath(GitPath path) {
		subpathMap.put(path.getName(), path);
	}
	
	public GitPath getSubpath(String name) {
		return subpathMap.get(name);
	}
	
	public GitFolder cd(String name) {
		GitPath subpath = getSubpath(name);
		assert subpath instanceof GitFolder;
		return (GitFolder)subpath;
	}
	
	public GitFile getFile(String name) throws FileNotFoundException, IOException {
		GitPath subpath = getSubpath(name);
		assert subpath instanceof GitFilepath;
		return ((GitFilepath)subpath).downCasting();
	}
	
	public Collection<GitPath> list() {
		return subpathMap.values();
	}
	
	protected GitFolder(GitCommit commit, String relativePath) throws IOException {
		super(commit, relativePath);
	}
}


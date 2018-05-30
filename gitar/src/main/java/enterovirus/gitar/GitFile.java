package enterovirus.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class GitFile extends GitPath {
	
	private byte[] blobContent;

	public byte[] getBlobContent() {
		return blobContent;
	}

	public GitFile(GitCommit commit, String relativePath) throws IOException, FileNotFoundException {
		
		super(commit, relativePath);
		
		RevTree revTree = commit.jGitCommit.getTree();
		try (TreeWalk treeWalk = new TreeWalk(commit.repository.jGitRepository)) {
			
			treeWalk.addTree(revTree);
			treeWalk.setRecursive(true);
			treeWalk.setFilter(PathFilter.create(relativePath));
			if (!treeWalk.next()) {
				/*
				 * If not do next(), always only get the first file.
				 * 
				 * Another note:
				 * 
				 * Previously use a runtime exception "IllegalStateException" 
				 * rather than "FileNotFoundException extends IOException".
				 * It is not working because it indeed sometimes want to query a
				 * file which may not exist (e.g., the configuration file 
				 * "gitenter.properties"). 
				 */
				throw new FileNotFoundException("Did not find expected file with relative path \""+relativePath+"\".");
			}
			ObjectLoader loader = commit.repository.jGitRepository.open(treeWalk.getObjectId(0));
			blobContent = loader.getBytes();
		}
	}
}

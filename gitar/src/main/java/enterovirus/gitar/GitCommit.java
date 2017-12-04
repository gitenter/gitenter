package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import enterovirus.gitar.identification.GitCommitSha;

public class GitCommit {

	public GitCommit (File repositoryDirectory, GitCommitSha commitSha) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitSha.getShaChecksumHash()));
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(false);
		while (treeWalk.next()) {
			if (treeWalk.isSubtree()) {
				System.out.println("dir: " + treeWalk.getPathString());
				treeWalk.enterSubtree();
			} else {
				System.out.println("file: " + treeWalk.getPathString());
			}
		}
	}
}

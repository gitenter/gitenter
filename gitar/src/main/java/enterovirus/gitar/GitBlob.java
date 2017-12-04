package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import enterovirus.gitar.identification.GitBranchName;
import enterovirus.gitar.identification.GitCommitSha;

public class GitBlob {
	
	byte[] blobContent;

	public GitBlob (File repositoryDirectory, GitCommitSha commitSha, String relativeFilepath) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitSha.getShaChecksumHash()));
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(true);
		System.out.println(treeWalk.getDepth());
		treeWalk.setFilter(PathFilter.create(relativeFilepath));
		if (!treeWalk.next()) {
			/*if not do next(), always only get the first file "test-add-a-file-from-client_1" */
			throw new IllegalStateException("Did not find expected file");
		}
		ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
		blobContent = loader.getBytes();
	}

	/**
	 * For the HEAD of a given branch. 
	 */
	public GitBlob (File repositoryDirectory, GitBranchName branchName, String relativeFilepath) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		Ref branch = repository.exactRef("refs/heads/"+branchName.getName());
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(branch.getObjectId());
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create(relativeFilepath));
		if (!treeWalk.next()) {
			/*if not do next(), always only get the first file "test-add-a-file-from-client_1" */
			throw new IllegalStateException("Did not find expected file");
		}
		ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
		blobContent = loader.getBytes();
	}
	
	/**
	 * For the HEAD of the master branch. 
	 */
	public GitBlob (File repositoryDirectory, String filePath) throws IOException {
		this(repositoryDirectory, new GitBranchName("master"), filePath);
	}
	public byte[] getBlobContent() {
		return blobContent;
	}
	
	/*
	 * TODO:
	 * 
	 * Using filename and upcasting to inheritance classes
	 * (e.g. Document file, pictures ...). Or should that
	 * be the job of capsid the web service??
	 */
}

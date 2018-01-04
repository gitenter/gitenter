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

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitBlob {

	private String relativeFilepath;
	private byte[] blobContent;

	public GitBlob (File repositoryDirectory, CommitSha commitSha, String relativeFilepath) throws IOException {
		
		this.relativeFilepath = relativeFilepath;
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitSha.getShaChecksumHash()));
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
	 * For the HEAD of a given branch. 
	 */
	public GitBlob (File repositoryDirectory, BranchName branchName, String relativeFilepath) throws IOException {

		this.relativeFilepath = relativeFilepath;
		
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
	public GitBlob (File repositoryDirectory, String relativeFilepath) throws IOException {
		this(repositoryDirectory, new BranchName("master"), relativeFilepath);
	}
	
	public byte[] getBlobContent() {
		return blobContent;
	}

	public String getRelativeFilepath() {
		return relativeFilepath;
	}
	
	/*
	 * TODO:
	 * 
	 * Using filename and upcasting to inheritance classes
	 * (e.g. Document file, pictures ...). Or should that
	 * be the job of capsid the web service??
	 */
}

package enterovirus.gitar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.commons.io.FilenameUtils;
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
import eu.medsea.mimeutil.MimeUtil;

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
	
	public byte[] getBlobContent() {
		return blobContent;
	}

	public String getRelativeFilepath() {
		return relativeFilepath;
	}
	
	public String getMimeType () throws IOException {
		
		InputStream is = new BufferedInputStream(new ByteArrayInputStream(blobContent));
		
		/*
		 * Refer to:
		 * https://stackoverflow.com/questions/51438/getting-a-files-mime-type-in-java/18640199
		 * https://stackoverflow.com/questions/33998407/how-to-fetch-the-mime-type-from-byte-array-in-java-6
		 * https://docs.oracle.com/javaee/5/api/javax/activation/MimetypesFileTypeMap.html
		 * https://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html
		 */
		String mimeType;
		
		mimeType = URLConnection.guessContentTypeFromStream(is);
		if (mimeType != null) {
			return mimeType;
		}
		
		mimeType = URLConnection.guessContentTypeFromName(relativeFilepath);
		if (mimeType != null) {
			return mimeType;
		}
		
		/*
		 * MimeUtil will get markdown MIME type "application/octet-stream",
		 * which is not correct.
		 */
		if (FilenameUtils.getExtension(relativeFilepath).equals("md")) {
			return "text/markdown";
		}
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeType = MimeUtil.getMimeTypes(is).iterator().next().toString();
		MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		if (mimeType != null) {
			return mimeType;
		}
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeType = MimeUtil.getMimeTypes(relativeFilepath).iterator().next().toString();
		MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		if (mimeType != null) {
			return mimeType;
		}
		
		return null;
	}
	
	/*
	 * TODO:
	 * 
	 * Using filename and upcasting to inheritance classes
	 * (e.g. Document file, pictures ...). Or should that
	 * be the job of capsid the web service??
	 */
}

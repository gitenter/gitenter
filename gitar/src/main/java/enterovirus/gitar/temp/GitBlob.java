package enterovirus.gitar.temp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import enterovirus.gitar.wrap.TagName;
import eu.medsea.mimeutil.MimeUtil;

public class GitBlob implements AutoCloseable {

	private String relativeFilepath;
	private byte[] blobContent;
	
	/*
	 * TODO:
	 * Write a better exception if the file doesn't exist in the corresponding commit.
	 */

	public GitBlob (File repositoryDirectory, CommitSha commitSha, String relativeFilepath) throws IOException {
		
		this.relativeFilepath = relativeFilepath;
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		writeToBlobContent(repository, ObjectId.fromString(commitSha.getShaChecksumHash()));
	}

	/**
	 * For the HEAD of a given branch. 
	 */
	public GitBlob (File repositoryDirectory, BranchName branchName, String relativeFilepath) throws IOException {

		this.relativeFilepath = relativeFilepath;
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		Ref branch = repository.exactRef("refs/heads/"+branchName.getName());
		writeToBlobContent(repository, branch.getObjectId());
	}

	public GitBlob (File repositoryDirectory, TagName tagName, String relativeFilepath) throws IOException {

		this.relativeFilepath = relativeFilepath;
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		Ref tag = repository.exactRef("refs/tags/"+tagName.getName());
		writeToBlobContent(repository, tag.getObjectId());
	}
	
	private void writeToBlobContent (Repository repository, ObjectId objectId) throws IOException {
		
		try (RevWalk revWalk = new RevWalk(repository)) {
		
			RevCommit commit = revWalk.parseCommit(objectId);
			RevTree revTree = commit.getTree();
		
			try (TreeWalk treeWalk = new TreeWalk(repository)) {
				
				treeWalk.addTree(revTree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(relativeFilepath));
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
					throw new FileNotFoundException("Did not find expected file with relative path \""+relativeFilepath+"\".");
				}
				ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
				blobContent = loader.getBytes();
			}
		}
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

	@Override
	public void close() {
		
		/*
		 * TODO:
		 * Shouldn't implemented anything in here. Since we already use try blocks
		 * so "RevWalk" and "TreeWalk" has already been properly closed.
		 * Double check!!
		 */
	}
}

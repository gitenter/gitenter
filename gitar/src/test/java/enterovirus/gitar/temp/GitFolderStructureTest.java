package enterovirus.gitar.temp;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import enterovirus.gitar.temp.GitBlob;
import enterovirus.gitar.temp.GitFolderStructure;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.gitar.wrap.TagName;

public class GitFolderStructureTest {
	
	
	@Test
	public void testSha1() throws IOException {
	
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		
		GitFolderStructure fs = new GitFolderStructure(repositoryDirectory, commitSha);
		
		System.out.println(fs.getCommitSha().getShaChecksumHash());
		showFolderStructure(fs);
		showDocumentList(fs);
	}
	
	@Test
	public void testSha2() throws IOException {
	
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_commit_traceability/org/repo.git");
		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_commit_traceability/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		
		GitFolderStructure fs = new GitFolderStructure(repositoryDirectory, commitSha);
		
		System.out.println(fs.getCommitSha().getShaChecksumHash());
		showFolderStructure(fs);
		showDocumentList(fs);
	}

	@Test
	public void testBranch() throws IOException {
		
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		BranchName branchName = new BranchName("master"); 
		
		GitFolderStructure fs = new GitFolderStructure(repositoryDirectory, branchName);
		
		System.out.println(fs.getCommitSha().getShaChecksumHash());
		showFolderStructure(fs);
	}

	@Test
	public void testTag() throws IOException {
		
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		TagName tagName = new TagName("first-commit");
		
		GitFolderStructure fs = new GitFolderStructure(repositoryDirectory, tagName);
		
		System.out.println(fs.getCommitSha().getShaChecksumHash());
		showFolderStructure(fs);
	}
	
	@Test
	public void testBranchWithFilter() throws IOException {
		
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_repo_fix_commit/org/repo.git");
		BranchName branchName = new BranchName("master"); 
		String[] includePaths = new String[]{"1st-commit-folder"};
		
		GitFolderStructure fs = new GitFolderStructure(repositoryDirectory, branchName, includePaths);
		
		System.out.println(fs.getCommitSha().getShaChecksumHash());
		showFolderStructure(fs);
	}
	
	@Test
	public void testShaWithFilter() throws IOException {
	
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_commit_traceability/org/repo.git");
		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_commit_traceability/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
		String[] includePaths = new String[]{"requirement", "design"};
		
		GitFolderStructure fs = new GitFolderStructure(repositoryDirectory, commitSha, includePaths);
		
		System.out.println(fs.getCommitSha().getShaChecksumHash());
		showFolderStructure(fs);
		showDocumentList(fs);
	}
	
	private void showFolderStructure (GitFolderStructure gitCommit) {
		System.out.println("====show folder structure====");
		showHierarchy(gitCommit.getFolderStructure(), 0);
	}
	
	private void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
		
		for (int i = 0; i < level; ++i) {
			System.out.print("\t");
		}
		System.out.println(parentNode);
		
//		Enumeration<TreeNode> e = parentNode.children();
////		while(e.hasMoreElements()) {
////			TreeNode node = e.nextElement();
////			showHierarchy(node);
////		}		
//		for(TreeNode node : Collections.list(e)) {
//			showHierarchy(node, level+1);
//		}
		
		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
			showHierarchy(node, level+1);
		}
	}
	
	private void showDocumentList (GitFolderStructure gitCommit) throws IOException {
		
		System.out.println("====show a list of blob files====");
		for (GitBlob blob : gitCommit.getGitBlobs()) {
			System.out.println(blob.getRelativeFilepath());
		}
	}
}
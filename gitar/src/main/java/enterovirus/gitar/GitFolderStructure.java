package enterovirus.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

public class GitFolderStructure {
	
	private CommitSha commitSha;

	/*
	 * JGit's "TreeWalk" class provides some simply functions
	 * to iterate some multi-child tree structure. However, that
	 * interface is really bad:
	 * 
	 * (1) It seems can only iterate once. There's no way to 
	 * re-navigate the tree structure or navigate it in a user
	 * defined way.
	 * 
	 * (2) Its "next()" walk to the next relevant entry but also
	 * return whether there's a next entry. There's no "hasNext()".
	 * 
	 * Therefore, we copy the tree structure out to another class
	 * (we use Swing's TreeModel and TreeNode -- although initially
	 * proposed to Swing's GUI to display, there's no reason to not
	 * just use the structure) and generate the folder structure
	 * navigating functions based on it. 
	 */
	private ListableMutableTreeNode folderStructure;
	
	public GitFolderStructure (File repositoryDirectory, CommitSha commitSha) throws IOException {
		
		this.commitSha = commitSha;
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitSha.getShaChecksumHash()));
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(false);
		
		generateDataFromTreeWalk(treeWalk);
	}

	public GitFolderStructure (File repositoryDirectory, BranchName branchName) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		Ref branch = repository.exactRef("refs/heads/"+branchName.getName());
		commitSha = new CommitSha(branch.getObjectId().getName());
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(branch.getObjectId());
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(false);
		
		generateDataFromTreeWalk(treeWalk);
	}
	
	public GitFolderStructure (File repositoryDirectory) throws IOException {
		this(repositoryDirectory, new BranchName("master"));
	}
	
	private void generateDataFromTreeWalk (TreeWalk treeWalk) throws IOException {
		
		folderStructure = new DefaultListableMutableTreeNode(".");
		
//		while (treeWalk.next()) {
//			if (treeWalk.isSubtree()) {
//				System.out.println("dir: " + treeWalk.getPathString()+"----"+"Depth:"+treeWalk.getDepth());
//				System.out.println("==Go into the subtree==");
//				treeWalk.enterSubtree();
//			} else {
//				System.out.println("file: " + treeWalk.getPathString()+"----"+"Depth:"+treeWalk.getDepth());
//			}
//		}
		
		/*
		 * Currently "treeWalk == null" unless we do one time
		 * "treeWalk.next()". There's an "if" for the case if
		 * the repository has no file at all.
		 */
		if (treeWalk.next()) {
			
			int position = 0;
			while (true) {
				GenerateTreeReturnValue returnValue = generateTree(treeWalk);
				folderStructure.insert(returnValue.node, position++);
				if (returnValue.hasNext == false) {
					break;
				}
			}
		}
	}

	private GenerateTreeReturnValue generateTree (TreeWalk treeWalk) throws IOException {
		
		ListableMutableTreeNode parentNode = new DefaultListableMutableTreeNode(treeWalk.getPathString());
		boolean hasNext;
		
		if (treeWalk.isSubtree()) {
			int depth = treeWalk.getDepth();
			
			treeWalk.enterSubtree();
			hasNext = treeWalk.next();
			
			int position = 0;
			while (true) {
				if (treeWalk.getDepth() <= depth) {
					break;
				}
				GenerateTreeReturnValue childReturnValue = generateTree(treeWalk);
				parentNode.insert(childReturnValue.node, position++);
				hasNext = childReturnValue.hasNext;	
			}
		}
		else {
			hasNext = treeWalk.next();
		}

		return new GenerateTreeReturnValue(parentNode, hasNext);
	}
	
	/*
	 * This method "generateTree" need to have two return values,
	 * one for whether there's a next element (due to the fact that
	 * JGit's "TreeWalk" only has a "next()" walk to the next 
	 * relevant entry but also return whether there's a next entry 
	 * but no "hasNext()".
	 */
	private class GenerateTreeReturnValue {

		private ListableMutableTreeNode node;
		private boolean hasNext;
		
		public GenerateTreeReturnValue(ListableMutableTreeNode node, boolean hasNext) {
			this.node = node;
			this.hasNext = hasNext;
		}
	}
	
	public CommitSha getCommitSha () {
		return commitSha;
	}
	
	public ListableTreeNode getFolderStructure () {
		return folderStructure;
	}
	
	/*
	 * TODO:
	 * Extend this to List<GitDocument> where
	 * GitDocument extends GitTextFile extends GitBlob
	 */
	public List<String> getDocuments() {
		
		List<String> documents = new ArrayList<String>();
		recursivelyIterateDocuments(folderStructure, documents);
		
		return documents;
	}
	
	private void recursivelyIterateDocuments (ListableTreeNode parentNode, List<String> documents) {

		if (parentNode.isLeaf()) {
			String filePath = parentNode.toString();
			/*
			 * TODO:
			 * Decide whether we should add this file by its filename format.
			 */
			documents.add(filePath);
			return;
		}
				
		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
			recursivelyIterateDocuments(node, documents);
		}
	}
	
	/*
	 * JSTL has no way to iterate "Enumeration" type, as it can
	 * (and should) only do immediate evaluation. See 
	 * https://stackoverflow.com/questions/256910/jstl-foreach-tag-problems-with-enumeration-and-with-understanding-how-it-shoul
	 * Therefore, we can only do "List" type.
	 * 
	 * On the other hand, "TreeNode" only has children() which
	 * gives back an "Enumeration" type. So we use the following
	 * trick to extend it to "ListableTreeNode" which makes JSTL
	 * possible.
	 */
	public interface ListableTreeNode extends TreeNode {
		public List<ListableTreeNode> childrenList();
	}
	
	private interface ListableMutableTreeNode extends MutableTreeNode, ListableTreeNode {
	}
	
	private class DefaultListableMutableTreeNode extends DefaultMutableTreeNode implements ListableMutableTreeNode {

		static final long serialVersionUID = 1L; 

		private DefaultListableMutableTreeNode(Object arg0) {
			super(arg0);
		}

		public List<ListableTreeNode> childrenList() {
			return Collections.list(this.children());
		}

	}
}

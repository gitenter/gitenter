package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import enterovirus.gitar.wrap.CommitSha;
import enterovirus.gitar.wrap.FileTreeModel;

public class GitCommit {
	
//	private List<FolderAndFilepath> folderAndFilepaths = new ArrayList<FolderAndFilepath>();

	private MutableTreeNode folderStructure;
	
	public GitCommit (File repositoryDirectory, CommitSha commitSha) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(repositoryDirectory).readEnvironment().findGitDir().build();
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(ObjectId.fromString(commitSha.getShaChecksumHash()));
		RevTree revTree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(revTree);
		treeWalk.setRecursive(false);
		
		folderStructure = new DefaultMutableTreeNode(repositoryDirectory.getPath());
		
//		while (treeWalk.next()) {
//			if (treeWalk.isSubtree()) {
//				System.out.println("dir: " + treeWalk.getPathString()+"----"+"Depth:"+treeWalk.getDepth());
//				treeWalk.enterSubtree();
//			} else {
//				System.out.println("file: " + treeWalk.getPathString()+"----"+"Depth:"+treeWalk.getDepth());
//			}
//		}
//		
//		System.out.println(treeWalk.next());
//		System.out.println(treeWalk.next());
//		System.out.println(treeWalk.getPathString());
//		System.out.println(treeWalk.next());
//		System.out.println(treeWalk.getPathString());
		
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
		
//		treeWalk.next();
//		while (treeWalk != null) {
//			System.out.println(treeWalk.getPathString());
////			if (treeWalk.getDepth() == 0) {
//				folderStructure.insert(generateTree(treeWalk), 0);
////			}
//		}
	}
	
//	private MutableTreeNode generateTree (TreeWalk treeWalk) throws IOException {
//		
//		MutableTreeNode parentNode = new DefaultMutableTreeNode(treeWalk.getPathString());
//		
//		if (treeWalk.isSubtree()) {
//			
//			treeWalk.enterSubtree();
//			
//			int depth = treeWalk.getDepth();
//			while (treeWalk.next()) {
//				if (treeWalk.getDepth() <= depth) {
//					break;
//				}
//				parentNode.insert(generateTree(treeWalk), 0);
//			}
//		}
//		else {
//			treeWalk.next();
//		}
//		
//		return parentNode;
//	}

	private GenerateTreeReturnValue generateTree (TreeWalk treeWalk) throws IOException {
		
		MutableTreeNode parentNode = new DefaultMutableTreeNode(treeWalk.getPathString());
		boolean hasNext;
		
		if (treeWalk.isSubtree()) {
			
			treeWalk.enterSubtree();
			
			int depth = treeWalk.getDepth();
			int position = 0;
			while (hasNext = treeWalk.next()) {
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
	
	private class GenerateTreeReturnValue {

		private MutableTreeNode node;
		private boolean hasNext;
		
		public GenerateTreeReturnValue(MutableTreeNode node, boolean hasNext) {
			this.node = node;
			this.hasNext = hasNext;
		}
	}
	
	private void showHierarchy (MutableTreeNode parentNode) {
		
		System.out.println(parentNode);
		
		Enumeration e = parentNode.children();
		while(e.hasMoreElements()) {
			MutableTreeNode node = (MutableTreeNode)e.nextElement();
			showHierarchy(node);
		}
	}
	
	public void showFolderStructure () {
		showHierarchy(folderStructure);
	}
	
//	public List<String> getFolderpaths () throws IOException {
//		
//		List<String> paths = new ArrayList<String>();
//		
////		treeWalk.reset();
//		while (treeWalk.next()) {
//			if (treeWalk.isSubtree()) {
//				paths.add(treeWalk.getPathString());
//			}
//		}
//		
//		return paths;	
//	}
//	
//	public List<String> getFilepaths () throws IOException {
//		
//		List<String> paths = new ArrayList<String>();
//		
////		treeWalk.reset();
//		while (treeWalk.next()) {
//			if (!treeWalk.isSubtree()) {
//				paths.add(treeWalk.getPathString());
//			}
//		}
//		
//		return paths;	
//	}
}

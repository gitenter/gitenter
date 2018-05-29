package enterovirus.gitar;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class GitFolder extends GitPath {
	
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
	
	public ListableMutableTreeNode getFolderStructure() {
		return folderStructure;
	}

	private static final String rootMarker = ".";

	public GitFolder(GitCommit gitCommit, String relativePath) throws IOException {
		
		super(gitCommit, relativePath);
		
		RevTree revTree = gitCommit.commit.getTree();
		try (TreeWalk treeWalk = new TreeWalk(gitCommit.gitRepository.repository)) {
			
			treeWalk.addTree(revTree);
			treeWalk.setRecursive(true);
			treeWalk.setFilter(PathFilter.create(relativePath));
			
			generateDataFromTreeWalk(treeWalk);
		}
	}

	

	private void generateDataFromTreeWalk (TreeWalk treeWalk) throws IOException {
		
		folderStructure = new DefaultListableMutableTreeNode(rootMarker);
		
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
	 * 
	 * Note: Non-static inner class only used for one particular 
	 * instance of the outer one.
	 */
	private class GenerateTreeReturnValue {

		private ListableMutableTreeNode node;
		private boolean hasNext;
		
		public GenerateTreeReturnValue(ListableMutableTreeNode node, boolean hasNext) {
			this.node = node;
			this.hasNext = hasNext;
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
	
	/*
	 * This is a "static nested class", because it is a general class
	 * not related to the particular instance of a GitFolderStructure instance.
	 */
	private static class DefaultListableMutableTreeNode extends DefaultMutableTreeNode implements ListableMutableTreeNode {

		static final long serialVersionUID = 1L; 

		private DefaultListableMutableTreeNode(Object arg0) {
			super(arg0);
		}

		public List<ListableTreeNode> childrenList() {
			return Collections.list(this.children());
		}

	}
}


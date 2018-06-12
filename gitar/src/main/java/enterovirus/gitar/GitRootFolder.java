package enterovirus.gitar;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GitRootFolder extends GitFolder {

	GitRootFolder(GitCommit commit) throws IOException {
		super(commit, ".");
	
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
		 */
		RevTree revTree = commit.jGitCommit.getTree();
		try (TreeWalk treeWalk = new TreeWalk(commit.repository.getJGitRepository())) {
			
			treeWalk.addTree(revTree);
			/*
			 * For "setRecursive(true)", it will flatten the entire tree
			 * structure, with "isSubtree()" is false. We shouldn't use
			 * this opinion.
			 */
			treeWalk.setRecursive(false);
			
			boolean hasNext = treeWalk.next();
			while(hasNext == true) {
				
				GitPathWrapper wrapper = build(treeWalk);
				addSubpath(wrapper.path);
				hasNext = wrapper.hasNext;
			}
		}
	}

	private GitPathWrapper build(TreeWalk treeWalk) throws IOException {
		
		boolean hasNext;
		
		if (treeWalk.isSubtree()) {
			
			GitFolder folder = new GitFolder(commit, treeWalk.getPathString());
			
			int depth = treeWalk.getDepth();
			
			treeWalk.enterSubtree();
			hasNext = treeWalk.next();
			
			while (treeWalk.getDepth() > depth) {
				
				GitPathWrapper wrapper = build(treeWalk);
				folder.addSubpath(wrapper.path);
				hasNext = wrapper.hasNext;
			}
			
			hasNext = treeWalk.next();
			return new GitPathWrapper(folder, hasNext);
		}
		else {
			GitFilepath file = new GitFilepath(commit, treeWalk.getPathString());
			
			hasNext = treeWalk.next();
			return new GitPathWrapper(file, hasNext);
		}
	}
	
	private class GitPathWrapper {
		
		private GitPath path;
		private boolean hasNext;
		
		public GitPathWrapper(GitPath path, boolean hasNext) {
			this.path = path;
			this.hasNext = hasNext;
		}
	}
}

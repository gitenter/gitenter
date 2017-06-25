package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class Main {
	
	public static String hello () {
		return "Hello enterovirus gitar!";
	}
	
	static void openGit (File file) throws IOException {

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repo = builder.setGitDir(file).readEnvironment().findGitDir().build();
		System.out.println(repo.getDirectory().toString());

		Ref master = repo.exactRef("refs/heads/master");
		System.out.println("Ref master============ "+master);
		System.out.println("Ref master ObjectId=========== "+master.getObjectId());
		System.out.println("Ref master target========== "+master.getTarget());
		System.out.println("\n\n");
		
		System.out.println("Last commit info (tree ObjectId + parent commit ObjectId + message)==========");
		ObjectLoader loader = repo.open(master.getObjectId());
		loader.copyTo(System.out);
		System.out.println("\n\n");
		
		System.out.println("Last commit byte=========");
		byte[] data = loader.getBytes();
		System.out.println(data);
		System.out.println("\n\n");
		
		RevWalk revWalk = new RevWalk(repo);
		RevCommit commit = revWalk.parseCommit(master.getObjectId());
		System.out.println("Last commit ObjectId======"+commit);
		
//		RevTree tree = walk.parseTree(commit.getTree().getId());
		RevTree tree = commit.getTree();
		System.out.println("Tree ObjectId========="+tree);
		System.out.println("\n\n");
		
		System.out.println("Tree info (show only the name of the first file???)=========");
		loader = repo.open(tree.getId());
		loader.copyTo(System.out);
		System.out.println("\n\n");
		
		System.out.println("File content=========");
		TreeWalk treeWalk = new TreeWalk(repo);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		treeWalk.setFilter(PathFilter.create("test-add-a-file-from-client_1"));
		loader = repo.open(treeWalk.getObjectId(0));
		loader.copyTo(System.out);
		
		revWalk.dispose();
		
	}
	
	public static void main(String[] args) throws IOException {
		
		openGit(new File("/home/beta/git/client_1/.git"));
		
//		System.out.println("Hello enterovirus gitar!");
	}
}

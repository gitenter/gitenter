package enterovirus.gitar.temp;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Constants;
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
		Repository repository = builder.setGitDir(file).readEnvironment().findGitDir().build();
		System.out.println(repository.getDirectory().toString());

		Ref master = repository.exactRef("refs/heads/master");
		System.out.println("Ref master============ "+master);
		System.out.println("Ref master ObjectId=========== "+master.getObjectId());
		System.out.println("Ref master target========== "+master.getTarget());
		System.out.println("\n\n");
		
		System.out.println("Last commit info (tree ObjectId + parent commit ObjectId + message)==========");
		ObjectLoader loader = repository.open(master.getObjectId());
		byte[] data = loader.getBytes();
		System.out.println(new String(data));
		System.out.println("\n\n");
		
		RevWalk revWalk = new RevWalk(repository);
		RevCommit commit = revWalk.parseCommit(master.getObjectId());
		System.out.println("Master ObjectId=========== "+master.getObjectId());
		System.out.println("Master commit Id======"+commit);
		System.out.println("Last commit ObjectId======="+repository.resolve(Constants.HEAD));
		System.out.println("\n\n");
		
//		RevTree tree = walk.parseTree(commit.getTree().getId());
		RevTree tree = commit.getTree();
		System.out.println("Tree ObjectId========="+tree);
		System.out.println("\n\n");
		
		System.out.println("Tree info (show only the name of the first file???)=========");
		loader = repository.open(tree.getId());
		loader.copyTo(System.out);
		System.out.println("\n\n");
		
		System.out.println("File content=========");
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
//		treeWalk.setFilter(PathFilter.create("test-add-a-file-from-client_1"));
//		treeWalk.setFilter(PathFilter.create("test-add-another-file"));
//		treeWalk.setFilter(PathFilter.create("same-name-file"));
		treeWalk.setFilter(PathFilter.create("folder_1/same-name-file"));
//		treeWalk.setFilter(null);
		if (!treeWalk.next()) {
			/*if not do next(), always only get the first file "test-add-a-file-from-client_1" */
			throw new IllegalStateException("Did not find expected file");
		}
		loader = repository.open(treeWalk.getObjectId(0));
		System.out.println(new String(loader.getBytes()));
//		loader.copyTo(System.out);
		
		revWalk.dispose();	
	}
	
	public static void main(String[] args) throws IOException {
//		System.out.println("Hello enterovirus gitar!");
	}
}

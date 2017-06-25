package enterovirus.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class Main {
	
	public static String hello () {
		return "Hello enterovirus gitar!";
	}
	
	static void openGit (File file) throws IOException {

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repo = builder.setGitDir(file).readEnvironment().findGitDir().build();
		System.out.println(repo.getDirectory().toString());

		Ref master = repo.exactRef("refs/heads/master");
		System.out.println(master.getObjectId().toString());
	}
	
	public static void main(String[] args) throws IOException {
		
		openGit(new File("/home/beta/git/client_1/.git"));
		
		System.out.println("Hello enterovirus gitar!");
	}
}

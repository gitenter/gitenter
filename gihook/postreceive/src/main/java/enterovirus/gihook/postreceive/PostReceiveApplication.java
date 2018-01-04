package enterovirus.gihook.postreceive;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import enterovirus.gihook.postreceive.status.CommitStatus;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.postreceive"})
public class PostReceiveApplication {
	
	@Autowired UpdateGitCommit updateGitCommit;
	
	public static void main (String[] args) throws Exception {

		File repositoryDirectory = new File(System.getProperty("user.dir"));
		
		/*
		 * Quote:
		 * 
		 * This hook executes once for the receive operation. It 
		 * takes no arguments, but for each ref to be updated it 
		 * receives on standard input a line of the format:
		 * 
		 * <old-value> SP <new-value> SP <ref-name> LF
		 * 
		 * https://git-scm.com/docs/githooks
		 */
		CommitSha oldCommitSha = new CommitSha(args[0]);
		CommitSha newCommitSha = new CommitSha(args[1]);
		BranchName branchName = new BranchName(args[2]);
		
		CommitStatus status = new CommitStatus(
				repositoryDirectory,
				branchName,
				oldCommitSha,
				newCommitSha);
		
		System.out.println("branchName: "+branchName.getName());
		System.out.println("oldCommitSha: "+oldCommitSha.getShaChecksumHash());
		System.out.println("newCommitSha: "+newCommitSha.getShaChecksumHash());
		
		System.setProperty("spring.profiles.active", "production");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PostReceiveApplication.class);
		
		PostReceiveApplication p = context.getBean(PostReceiveApplication.class);
		p.run(status);
	}
	
	private void run (CommitStatus status) throws IOException, GitAPIException {
		updateGitCommit.apply(status);
	}
}

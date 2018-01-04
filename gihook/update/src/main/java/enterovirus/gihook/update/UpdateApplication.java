package enterovirus.gihook.update;

import java.io.File;
import org.springframework.context.annotation.ComponentScan;

import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitSha;

@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.update"})
public class UpdateApplication {
	
	public static void main (String[] args) throws Exception {

		File repositoryDirectory = new File(System.getProperty("user.dir"));
		BranchName branchName = new BranchName(args[0]);
		CommitSha oldCommitSha = new CommitSha(args[1]);
		CommitSha newCommitSha = new CommitSha(args[2]);
		
		System.out.println("repositoryDirectory: "+repositoryDirectory.getAbsolutePath());
		System.out.println("branchName: "+branchName.getName());
		System.out.println("oldCommitSha: "+oldCommitSha.getShaChecksumHash());
		System.out.println("newCommitSha: "+newCommitSha.getShaChecksumHash());
	}
}

package enterovirus.gihook.update;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.gihook.update.status.CommitStatus;
import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.update"})
public class UpdateApplication {
	
//	@Autowired private RepositoryRepository repositoryRepository;
//	@Autowired private CommitRepository commitRepository;
	
	public static void main (String[] args) throws Exception {

		File repositoryDirectory = new File(System.getProperty("user.dir"));
		BranchName branchName = new BranchName(args[0]);
		CommitSha oldCommitSha = new CommitSha(args[1]);
		CommitSha newCommitSha = new CommitSha(args[2]);
		
		CommitStatus status = new CommitStatus(
				repositoryDirectory,
				branchName,
				oldCommitSha,
				newCommitSha);
		
		System.out.println("branchName: "+branchName.getName());
		System.out.println("oldCommitSha: "+oldCommitSha.getShaChecksumHash());
		System.out.println("newCommitSha: "+newCommitSha.getShaChecksumHash());
		
		System.setProperty("spring.profiles.active", "production");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(UpdateApplication.class);
		
		UpdateApplication p = context.getBean(UpdateApplication.class);
//		p.run(status);
	}
	
//	private void run (CommitStatus status) throws IOException, GitAPIException {
//		updateGitCommits(status);
//	}
//	
//	/*
//	 * TODO:
//	 * Cannot do "private". Otherwise cannot initialize lazy evaluation of "commits".
//	 * Don't understand why.
//	 * 
//	 * TODO:
//	 * Move the relevant functions to some other classes, such as some controllers. 
//	 */
//	@Transactional
//	public void updateGitCommits (CommitStatus status) throws IOException, GitAPIException {
//		
//		GitLog gitLog = new GitLog(status.getRepositoryDirectory(), status.getBranchName(), status.getOldCommitSha(), status.getNewCommitSha());
//	
//		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(status.getOrganizationName(), status.getRepositoryName());
//		Hibernate.initialize(repository.getCommits());
//		
//		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
//			
////			GitFolderStructure gitCommit = new GitFolderStructure(repositoryDirectory, commitInfo.getCommitSha());
////			showFolderStructure(gitCommit);
////			
//			CommitBean commit = new CommitBean(repository, commitInfo.getCommitSha());
//			repository.addCommit(commit);
//		}
//		
//		repositoryRepository.saveAndFlush(repository);
//	}
}

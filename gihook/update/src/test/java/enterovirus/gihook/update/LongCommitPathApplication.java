package enterovirus.gihook.update;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import enterovirus.gihook.update.config.UpdateDatabaseConfig;
import enterovirus.gihook.update.config.UpdateGitConfig;
import enterovirus.gihook.update.status.CommitStatus;
import enterovirus.gitar.GitFolderStructure;
import enterovirus.gitar.GitLog;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.BranchName;
import enterovirus.gitar.wrap.CommitInfo;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.ProteaseConfig;
import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;

@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.update"})
public class LongCommitPathApplication {
	
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private CommitRepository commitRepository;
	
	public static void main (String[] args) throws Exception {
		
		File repositoryDirectory = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/org/repo.git");
		File commitRecordFileMaster = new File("/home/beta/Workspace/enterovirus-test/long-commit-path/commit-sha-list-master.txt");
		
		CommitStatus status = new CommitStatus(
				repositoryDirectory,
				new BranchName("master"),
				new CommitSha(commitRecordFileMaster, 1),
				new CommitSha(commitRecordFileMaster, 10));
		
		/*
		 * We need to active the Spring profile definition for 
		 * "dataSource" and "gitSource".
		 * 
		 * "spring.profiles.active" system property is the only
		 * working we I know until now.
		 */
		System.setProperty("spring.profiles.active", "long_commit_path");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LongCommitPathApplication.class);
		/*
		 * It is not good because it hard code system property.
		 *  
		 * There should be a better way rather than hard coding
		 * it. The following post suggest a way using
		 * "setActiveProfile":
		 * https://dzone.com/articles/using-spring-profiles-and-java
		 * https://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile/
		 * 
		 * However, for me it raises errors in the
		 * "new AnnotationConfigApplicationContext" part.
		 * 
		 * Lucky, this is only for testing. For real application
		 * we only have one single dataSource so it is easier.
		 */
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.register(ProteaseConfig.class, UpdateConfig.class);
//		context.getEnvironment().setActiveProfiles("long_commit_path");
//		context.refresh();
		/*
		 * What suggests in the following link seems also doesn't work:
		 * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html
		 */
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.register(UpdateDatabaseConfig.class, UpdateGitConfig.class);
//		context.getEnvironment().setActiveProfiles("long_commit_path");
//		context.refresh();
		
		LongCommitPathApplication p = context.getBean(LongCommitPathApplication.class);
		p.run(status);
	}
	
	private void run (CommitStatus status) throws IOException, GitAPIException {
		updateGitCommits(status);
	}
	
	/*
	 * TODO:
	 * Cannot do "private". Otherwise cannot initialize lazy evaluation of "commits".
	 * Don't understand why.
	 * 
	 * TODO:
	 * Move the relevant functions to some other classes, such as some controllers. 
	 */
	@Transactional
	public void updateGitCommits (CommitStatus status) throws IOException, GitAPIException {
		
		GitLog gitLog = new GitLog(status.getRepositoryDirectory(), status.getBranchName(), status.getOldCommitSha(), status.getNewCommitSha());
	
		RepositoryBean repository = repositoryRepository.findByOrganizationNameAndRepositoryName(status.getOrganizationName(), status.getRepositoryName());
		Hibernate.initialize(repository.getCommits());
		
		for (CommitInfo commitInfo : gitLog.getCommitInfos()) {
			
//			GitFolderStructure gitCommit = new GitFolderStructure(repositoryDirectory, commitInfo.getCommitSha());
//			showFolderStructure(gitCommit);
//			
			CommitBean commit = new CommitBean(repository, commitInfo.getCommitSha());
			repository.addCommit(commit);
		}
		
		repositoryRepository.saveAndFlush(repository);
	}
	
//	private static void showFolderStructure (GitFolderStructure gitCommit) {
//		showHierarchy(gitCommit.getFolderStructure(), 0);
//	}
//	
//	private static void showHierarchy (GitFolderStructure.ListableTreeNode parentNode, int level) {
//		
//		for (int i = 0; i < level; ++i) {
//			System.out.print("\t");
//		}
//		System.out.println(parentNode);
//		
//		for(GitFolderStructure.ListableTreeNode node : parentNode.childrenList()) {
//			showHierarchy(node, level+1);
//		}
//	}
}

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

@ComponentScan(basePackages = {
		"enterovirus.protease",
		"enterovirus.gihook.postreceive"})
public class LongCommitPathApplication {
	
	@Autowired UpdateGitCommit updateGitCommit;
	
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
		updateGitCommit.apply(status);
	}
}

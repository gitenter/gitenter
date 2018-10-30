package com.gitenter.hook.postreceive;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.gitenter.hook.postreceive.service.HookInputSet;
import com.gitenter.hook.postreceive.service.UpdateDatabaseFromGitService;
import com.gitenter.hook.postreceive.service.UpdateDatabaseFromGitServiceImpl;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan(basePackages = {"com.gitenter.hook.postreceive", "com.gitenter.protease"})
public class PostReceiveApplication {
	
	@Autowired private UpdateDatabaseFromGitService updateDatabaseFromGitService;
	
	public static void main (String[] args) throws Exception {
		
		HookInputSet input = new HookInputSet(System.getProperty("user.dir"), args);
		
		System.out.println("branchName: "+input.getBranchName());
		System.out.println("oldCommitSha: "+input.getOldSha());
		System.out.println("newCommitSha: "+input.getNewSha());
		
//		/*
//		 * We need to active the Spring profile definition for 
//		 * "dataSource" and "gitSource".
//		 * 
//		 * "spring.profiles.active" system property is the only
//		 * working way I know until now.
//		 */
		System.setProperty("spring.profiles.active", "production");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PostReceiveApplication.class);
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
//		context.register(ProteaseConfig.class, PostReceiveConfig.class);
//		context.getEnvironment().setActiveProfiles("long_commit_path");
//		context.refresh();
		/*
		 * What suggests in the following link seems also doesn't work:
		 * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html
		 */
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//		context.register(DatabaseConfig.class, GitConfig.class);
//		context.getEnvironment().setActiveProfiles("production");
//		context.refresh();
		
		PostReceiveApplication p = context.getBean(PostReceiveApplication.class);
		p.run(input);
	}
	
	private void run (HookInputSet input) throws IOException, GitAPIException {
		updateDatabaseFromGitService.update(input);
	}
}

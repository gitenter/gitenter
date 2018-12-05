package com.gitenter.hook.postreceive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.gitenter.hook.postreceive.service.HookInputSet;
import com.gitenter.hook.postreceive.service.UpdateDatabaseFromGitService;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.gitenter.hook.postreceive", 
		"com.gitenter.protease"})
public class PostReceiveApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		SpringApplication.run(PostReceiveApplication.class, args);
	}
	
	@Autowired private UpdateDatabaseFromGitService updateDatabaseFromGitService;
	
	@Override
	public void run(String... args) throws Exception {
		
		HookInputSet input = new HookInputSet(System.getProperty("user.dir"), args);
		
		System.out.println("branchName: "+input.getBranchName());
		System.out.println("oldCommitSha: "+input.getOldSha());
		System.out.println("newCommitSha: "+input.getNewSha());
		
		/*
		 * We need to active the Spring profile definition for 
		 * "dataSource" and "gitSource".
		 * 
		 * "spring.profiles.active" system property is the only
		 * working way I know until now.
		 * 
		 * TODO:
		 * This need to be changed based on what profile of the
		 * web application (envelope) is using. It is quite troublesome. 
		 * Should be optimized so at least no need to change in multiple 
		 * times.
		 */
//		System.setProperty("spring.profiles.active", "sts");
//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PostReceiveApplication.class);
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
		
		updateDatabaseFromGitService.update(input);
	}
}

package com.gitenter.post_receive_hook;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.gitenter.post_receive_hook.service.HookInputSet;
import com.gitenter.post_receive_hook.service.UpdateDatabaseFromGitService;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed.
 */
@ComponentScan(basePackages = {"com.gitenter.post_receive_hook", "com.gitenter.protease"})
public class PostReceiveApplication {

	@Autowired private UpdateDatabaseFromGitService updateDatabaseFromGitService;

	public static void main(String[] args) throws Exception {

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
		 * web application (capsid) is using. It is quite troublesome.
		 * Should be optimized so at least no need to change in multiple
		 * times.
		 */
		System.setProperty("spring.profiles.active", "sts");
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
//		context.register(DatabaseConfig.class, GitSourceConfig.class);
//		context.getEnvironment().setActiveProfiles("production");
//		context.refresh();

		PostReceiveApplication p = context.getBean(PostReceiveApplication.class);
		p.run(input);
	}

	private void run(HookInputSet input) throws IOException, GitAPIException {
		updateDatabaseFromGitService.update(input);
	}
}

package enterovirus.gihook.precommit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"enterovirus.protease","enterovirus.gihook.precommit"})
public class PreCommitApplication {
	
	@Autowired private CheckGitStatus checkGitStatus;
	
	public static void main (String[] args) throws Exception {
		
		File repositoryDirectory = new File(System.getProperty("user.dir"));
		
		System.setProperty("spring.profiles.active", "production");
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PreCommitApplication.class);
		PreCommitApplication p = context.getBean(PreCommitApplication.class);
		p.run(repositoryDirectory);
	}
	
	private void run (File repositoryDirectory) throws IOException, GitAPIException {
		
		String returnMessage = checkGitStatus.apply(repositoryDirectory);
		
		System.out.println("Return message: "+returnMessage);
	}
}

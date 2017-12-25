package enterovirus.protease;

import java.io.File;
import java.io.IOException;

import enterovirus.gitar.GitCommit;
import enterovirus.gitar.GitSource;
import enterovirus.gitar.wrap.CommitSha;
import enterovirus.protease.database.Tmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/*
 * This main class has nothing to do with unit tests.
 * If this package is used as a library rather than a
 * stand-alone executive jar, then this class is not
 * needed. 
 */
@ComponentScan
public class Application {
	
	@Autowired Tmp tmp;
	
	private void run () {
		System.out.println("hello world");
		System.out.println(tmp.find());
	}
	
	public static void main (String[] args) throws IOException {
		
		/*
		 * Cannot use the general "GenericApplicationContext"
		 * as it is not auto-closable.
		 */
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class)) {
			Application p = context.getBean(Application.class);
			p.run();
		}
	}
}

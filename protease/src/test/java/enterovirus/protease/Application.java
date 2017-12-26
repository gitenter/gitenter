package enterovirus.protease;

import java.io.IOException;

import enterovirus.protease.database.OrganizationRepository;
import enterovirus.protease.domain.OrganizationBean;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired private OrganizationRepository organizationRepository;
	
	private void run () {	
		OrganizationBean organization = organizationRepository.findByName("org1").get(0);
		System.out.println(organization.getDisplayName());
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

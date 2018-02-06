package enterovirus.immunessh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import enterovirus.protease.database.RepositoryMemberMapRepository;
import enterovirus.protease.domain.RepositoryMemberMapBean;
import enterovirus.protease.domain.RepositoryMemberRole;

@ComponentScan(basePackages = {"enterovirus.protease","enterovirus.immunessh"})
public class ImmuneSshApplication {
	
	@Autowired RepositoryMemberMapRepository repository;
	
	public static void main (String[] args) throws Exception {
		
		String username = args[0];
		String organizationName = args[1];
		String repositoryName = args[2];
		
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ImmuneSshApplication.class);
		ImmuneSshApplication p = context.getBean(ImmuneSshApplication.class);
		
		/*
		 * Print "true" if the user can edit the repository.
		 * Print "false" if the user cannot.
		 */
		System.out.println(p.run(username, organizationName, repositoryName));
	}
	
	private boolean run (String username, String organizationName, String repositoryName) {
		
		List<RepositoryMemberMapBean> maps = repository.findByUsernameAndOrganizationNameAndRepositoryName(username, organizationName, repositoryName);

		/*
		 * It should be at most one element in map, but we write in 
		 * here in a general way.
		 */
		if (maps.size() >= 1) {
			
			RepositoryMemberRole role = maps.get(0).getRole();
			
			if (role.equals(RepositoryMemberRole.PROJECT_LEADER) || role.equals(RepositoryMemberRole.EDITOR)) {
				return true;
			}
		}
		
		return false;
	}
}

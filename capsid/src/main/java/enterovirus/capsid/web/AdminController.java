package enterovirus.capsid.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import enterovirus.capsid.database.*;
import enterovirus.capsid.domain.*;
import enterovirus.gitar.GitRepository;
import enterovirus.gitar.GitSource;

@Controller
public class AdminController {

	@Autowired private MemberRepository memberRepository;
	@Autowired private OrganizationRepository organizationRepository;
	@Autowired private RepositoryRepository repositoryRepository;
	@Autowired private GitSource gitSource;
	
	/*
	 * If the user is not the manager of this repository,
	 * then this cannot be done.
	 */
	private void isManagerCheck (Authentication authentication, OrganizationBean organization) {
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		if (!organization.isManagedBy(member.getId())) {
			throw new AccessDeniedException("You are not authorized as a manager of this organization.");
		}
	}
	
	@RequestMapping(value="/organizations/create", method=RequestMethod.GET)
	public String createOrganization (Model model) {

		model.addAttribute("organizationBean", new OrganizationBean());
		return "admin/create-organization";
	}
	
	@RequestMapping(value="/organizations/create", method=RequestMethod.POST)
	public String processCreationOfOrganization (
			@Valid OrganizationBean organization, 
			Errors errors, 
			Model model,
			Authentication authentication) {
		
		if (errors.hasErrors()) {
			/* So <sf:> will render the values in object "member" to the form. */
			model.addAttribute("organizationBean", organization); 
			return "admin/create-organization";
		}
		
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		List<MemberBean> managers = new ArrayList<MemberBean>();
		managers.add(member);
		organization.setManagers(managers);
		
		organizationRepository.saveAndFlush(organization);
		return "redirect:/";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.GET)
	public String createRepository (
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) {

		OrganizationBean organization = organizationRepository.findById(organizationId).get(0);
		
		isManagerCheck(authentication, organization);
		
		model.addAttribute("organization", organization);
		model.addAttribute("repositoryBean", new RepositoryBean());
		return "admin/create-repository";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.POST)
	public String processCreationOfRepository (
			@PathVariable Integer organizationId,
			@Valid RepositoryBean repository, 
			Errors errors, 
			Model model,
			Authentication authentication) throws GitAPIException {
		
		OrganizationBean organization = organizationRepository.findById(organizationId).get(0);
		
		isManagerCheck(authentication, organization);
		
		if (errors.getErrorCount() > 1) {
			/*
			 * Because "gitUri" is not set up by the form, it at least 
			 * contribute "1" for the error count.
			 */
			
			/* So <sf:> will render the values in object "member" to the form. */
			model.addAttribute("repositoryBean", repository); 
			model.addAttribute("organization", organization);
			return "admin/create-repository";
		}
		
		repository.setOrganization(organization);
		
		/*
		 * Setup git URI which follows the GitSource format.
		 */
		File gitUri = gitSource.getRepositoryDirectory(organization.getName(), repository.getName());
		repository.setGitUri(gitUri.toString());
		
		GitRepository.initBare(gitUri);
		
		repositoryRepository.saveAndFlush(repository);
		return "redirect:/organizations/"+organizationId;
	}
	
	/*
	 * TODO:
	 * Manager management (add/remove managers).
	 */
}
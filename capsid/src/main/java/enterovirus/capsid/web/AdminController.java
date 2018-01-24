package enterovirus.capsid.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;
import enterovirus.protease.source.GitSource;
import enterovirus.gitar.*;
import enterovirus.gitar.wrap.*;

@Controller
public class AdminController {

	@Autowired private MemberRepository memberRepository;
	@Autowired private OrganizationRepository organizationRepository;
	@Autowired private RepositoryRepository repositoryRepository;
	
	/*
	 * TODO:
	 * Should all calls go through the persistence layer
	 * rather than from gitSource?
	 */
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
			Authentication authentication) throws Exception {
		
		if (errors.hasErrors()) {
			/* So <sf:> will render the values in object "member" to the form. */
			model.addAttribute("organizationBean", organization); 
			return "admin/create-organization";
		}
		
		MemberBean member = memberRepository.findByUsername(authentication.getName()).get(0);
		List<MemberBean> managers = new ArrayList<MemberBean>();
		managers.add(member);
		organization.setManagers(managers);
		
		/*
		 * The "mkdir" part include "chmod" and "chown".
		 */
		gitSource.mkdirOrganizationDirectory(organization.getName());
		
		organizationRepository.saveAndFlush(organization);
		return "redirect:/";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.GET)
	public String createRepository (
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws IOException {

		Optional<OrganizationBean> organizations = organizationRepository.findById(organizationId);
		if (!organizations.isPresent()) {
			throw new IOException ("organizationId does not exist!");
		}
		OrganizationBean organization = organizations.get();
		
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
			Authentication authentication) throws GitAPIException, IOException {
		
		Optional<OrganizationBean> organizations = organizationRepository.findById(organizationId);
		if (!organizations.isPresent()) {
			throw new IOException ("organizationId does not exist!");
		}
		OrganizationBean organization = organizations.get();
		
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
		 * The "mkdir" part include "chmod" and "chown".
		 */
		gitSource.mkdirBareRepositoryDirectory(organization.getName(), repository.getName());
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organization.getName(), repository.getName());
		repository.setGitUri(repositoryDirectory.toString());
		
		ClassLoader classLoader = getClass().getClassLoader();
		File sampleHooksDirectory = new File(classLoader.getResource("git-server-side-hooks").getFile());
		File configFilesDirectory = new File(classLoader.getResource("config-files").getFile());

		GitRepository.initBareWithConfig(repositoryDirectory, sampleHooksDirectory, configFilesDirectory);
		
		/*
		 * Dirty but this part can only be done in here. See comments under GitRepository.
		 */
		GitLog gitLog = new GitLog(repositoryDirectory, new BranchName("master"), 1, 0);
		CommitSha commitSha = gitLog.getCommitInfos().get(0).getCommitSha();
		CommitBean commit = new CommitValidBean(repository, commitSha);
		repository.addCommit(commit);
		
		repositoryRepository.saveAndFlush(repository);
		return "redirect:/organizations/"+organizationId;
	}
	
	/*
	 * TODO:
	 * Manager management (add/remove managers).
	 */
}

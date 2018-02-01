package enterovirus.capsid.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.hibernate.Hibernate;
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
	@Autowired private RepositoryMemberMapRepository repositoryMemberMapRepository;
	
	/*
	 * TODO:
	 * Should all calls go through the persistence layer
	 * rather than from gitSource?
	 */
	@Autowired private GitSource gitSource;
	
	/*
	 * If the user is not the manager of this repository,
	 * then this cannot be done.
	 * 
	 * This is generally not needed for POST requests,
	 * as they all comes from GET request of forms with
	 * CSRF key included.
	 */
	private void isManagerCheck (Authentication authentication, OrganizationBean organization) throws IOException {
		MemberBean member = memberRepository.findByUsername(authentication.getName());
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
			model.addAttribute("organizationBean", organization); 
			return "admin/create-organization";
		}
		
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		List<MemberBean> managers = new ArrayList<MemberBean>();
		managers.add(member);
		organization.setManagers(managers);
		
		gitSource.mkdirOrganizationDirectory(organization.getName());
		
		organizationRepository.saveAndFlush(organization);
		return "redirect:/";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.GET)
	public String createRepository (
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws Exception {

		OrganizationBean organization = organizationRepository.findById(organizationId);
		
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
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		
		isManagerCheck(authentication, organization);
		
		if (errors.hasErrors()) {
			model.addAttribute("repositoryBean", repository); 
			model.addAttribute("organization", organization);
			return "admin/create-repository";
		}
		
		repository.setOrganization(organization);
		
		/*
		 * TODO:
		 * 
		 * Here makes a bare repository with one commit (include the
		 * configuration file) in it.
		 * 
		 * The other possibility is (in the client side) to add the 
		 * configuration file into an existing git repository.
		 * Then in here we just want to "mkdir" but do not need to
		 * "git init". 
		 * 
		 * Should give the user the choices in the UI.
		 */
		gitSource.mkdirBareRepositoryDirectory(organization.getName(), repository.getName());
		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organization.getName(), repository.getName());
		
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
	
	@RequestMapping(value="/organizations/{organizationId}/managers", method=RequestMethod.GET)
	public String manageOrganizationManagers (
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws IOException {
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		Hibernate.initialize(organization.getManagers());
		
		/*
		 * Only need to be in here. No need for the corresponding POST
		 * requests, as CSRF key is included.
		 */
		isManagerCheck(authentication, organization);
		
		model.addAttribute("organization", organization);
		return "admin/organization-managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/managers/add", method=RequestMethod.POST)
	public String addAOrganizationManager (
			@PathVariable Integer organizationId,
			String username,
			Authentication authentication) throws Exception {
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		Hibernate.initialize(organization.getManagers());
		
		MemberBean newManager = memberRepository.findByUsername(username);
		organization.addManager(newManager);
		
		/*
		 * TODO:
		 * Raise errors and redirect to the original page,
		 * if the manager username is invalid.
		 */
		
		organizationRepository.saveAndFlush(organization);
		
		return "redirect:/organizations/"+organizationId+"/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/managers/{memberId}/remove", method=RequestMethod.POST)
	public String removeAOrganizationManager (
			@PathVariable Integer organizationId,
			@PathVariable Integer memberId,
			Authentication authentication) throws Exception {
		
		/*
		 * TODO:
		 * Should not be able to remove somebody herself.
		 */
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		
		Hibernate.initialize(organization.getManagers());
		organization.removeManager(memberId);
		
		organizationRepository.saveAndFlush(organization);
		
		return "redirect:/organizations/"+organizationId+"/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/collaborators", method=RequestMethod.GET)
	public String manageRepositoryCollaborators (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model,
			Authentication authentication) throws Exception {
		
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		/*
		 * Only need to be in here. No need for the corresponding POST
		 * requests, as CSRF key is included.
		 */
		isManagerCheck(authentication, organization);
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		
		model.addAttribute("repositoryMemberRoleValues", RepositoryMemberRole.values());
		
		return "admin/repository-collaborators";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/collaborators/add", method=RequestMethod.POST)
	public String addARepositoryCollaborator (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			String username,
			String role) throws Exception {
		
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		
		MemberBean newCollaborator = memberRepository.findByUsername(username);
		repository.addMember(newCollaborator, RepositoryMemberRole.valueOf(role));
		
		/*
		 * TODO:
		 * Raise errors and redirect to the original page,
		 * if the collaborator manager username is invalid.
		 */
		
		repositoryRepository.saveAndFlush(repository);
		
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/collaborators";
	}
	
	@Transactional
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/collaborators/{mapId}/remove", method=RequestMethod.POST)
	public String removeARepositoryCollaborator (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			/*
			 * NOTE: 
			 * Here is mapId rather than memberId. See explanation
			 * below why that's the case.
			 */
			@PathVariable Integer mapId,
			Authentication authentication) throws Exception {

		/*
		 * Delete the corresponding map in RepositoryBean and 
		 * repositoryRepository.saveAndFlush(repository)
		 * does not working, because repositoryRepository.saveAndFlush() cannot 
		 * really follow this change of another table (with is not the @ManyToMany
		 * case).
		 * 
		 * Delete from "memberId" and fine "mapId" from it is
		 * (1) need more SQL queries, (2) seems have consistency problem with Hibernate
		 * when first we "Hibernate.initialize(repository.getRepositoryMemberMaps());".
		 * Therefore, we make it easy to just delete by map Id.
		 */
		repositoryMemberMapRepository.deleteById(mapId);

		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/collaborators";
	}
}

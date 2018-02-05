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
import org.springframework.web.bind.annotation.RequestParam;
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
	private MemberBean isManagerCheck (Authentication authentication, OrganizationBean organization) throws IOException {
		MemberBean self = memberRepository.findByUsername(authentication.getName());
		if (!organization.isManagedBy(self.getId())) {
			throw new AccessDeniedException("You are not authorized as a manager of this organization.");
		}
		
		return self;
	}
	
	@RequestMapping("/")
	public String main(Model model, Authentication authentication) throws Exception {
		
		/*
		 * The organizations the member acts as a manager.
		 */
		MemberBean member = memberRepository.findByUsername(authentication.getName());
		List<OrganizationBean> organizations = member.getOrganizations();
		model.addAttribute("organizations", organizations);
		
		/*
		 * TODO:
		 * Should also add repositories the member has (some
		 * kind of) access to.
		 */
		
		return "main";
	}
	
	@RequestMapping(value="/organizations/{organizationId}", method=RequestMethod.GET)
	public String showOrganization (
			@PathVariable Integer organizationId,
			Authentication authentication,
			Model model) throws IOException {
	
		/*
		 * TODO:
		 * For private contents, only users who belong to that
		 * organization can see the materials.
		 */
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		MemberBean self = memberRepository.findByUsername(authentication.getName());
		
		if (organization.isManagedBy(self.getId())) {
			model.addAttribute("isManager", true);
		}
		
		Hibernate.initialize(organization.getManagers());
		model.addAttribute("organization", organization);
		
		return "organization";
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
			@RequestParam(value="include_setup_files") Boolean includeSetupFiles,
			Errors errors, 
			Model model,
			Authentication authentication) throws GitAPIException, IOException {
		
		System.out.println("X"+includeSetupFiles);
		OrganizationBean organization = organizationRepository.findById(organizationId);
		
		isManagerCheck(authentication, organization);
		
		if (errors.hasErrors()) {
			model.addAttribute("repositoryBean", repository); 
			model.addAttribute("organization", organization);
			return "admin/create-repository";
		}
		
		repository.setOrganization(organization);
		
		gitSource.mkdirBareRepositoryDirectory(organization.getName(), repository.getName());
		
		/*
		 * Here makes a bare repository with one commit (include the
		 * configuration file) in it.
		 */
		if (includeSetupFiles.equals(Boolean.TRUE)) {
			
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
		}
		
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
		MemberBean self = isManagerCheck(authentication, organization);
		
		/*
		 * This "self" is used to check if the user want to remove himself/herself
		 * as a manager. If yes, then the remove link will not be shown.
		 */
		model.addAttribute("self", self);
		model.addAttribute("organization", organization);
		
		return "admin/organization-managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/managers/add", method=RequestMethod.POST)
	public String addAOrganizationManager (
			@PathVariable Integer organizationId,
			@RequestParam(value="username") String username,
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
	
	@RequestMapping(value="/organizations/{organizationId}/managers/remove", method=RequestMethod.POST)
	public String removeAOrganizationManager (
			@PathVariable Integer organizationId,
			@RequestParam(value="member_id") Integer memberId,
			Authentication authentication,
			Model model) throws Exception {
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		Hibernate.initialize(organization.getManagers());
		
		/*
		 * It is kind of double check, as manageOrganizationManagers()
		 * doesn't really show the link to remove the user himself/herself.
		 * But it is a safe choice. As all forms in the same page share the
		 * same CSRF key, the user may actually hack the system.
		 */
		MemberBean self = memberRepository.findByUsername(authentication.getName());
		if (self.getId().equals(memberId)) {
			
			model.addAttribute("organization", organization);
			model.addAttribute("errorMessage", "Could not remove yourself as a manager.");
			
			return "admin/organization-managers";
		}
		
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
			@RequestParam(value="username") String username,
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
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/collaborators/remove", method=RequestMethod.POST)
	public String removeARepositoryCollaborator (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			/*
			 * NOTE: 
			 * Here is mapId rather than memberId. See explanation
			 * below why that's the case.
			 */
			@RequestParam(value="repository_member_map_id") Integer repositoryMemberMapId,
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
		repositoryMemberMapRepository.deleteById(repositoryMemberMapId);

		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/collaborators";
	}
}

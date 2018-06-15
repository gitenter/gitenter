package enterovirus.capsid.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gitenter.gitar.*;
import com.gitenter.gitar.temp.GitLog;
import com.gitenter.gitar.temp.GitRepository;
import com.gitenter.gitar.wrap.*;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;

import enterovirus.protease.database.*;
import enterovirus.protease.domain.*;
import enterovirus.protease.source.GitSource;

@Controller
public class ManagerAdminController {

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
	
	@RequestMapping(value="/organizations/{organizationId}/managers", method=RequestMethod.GET)
	public String manageOrganizationManagers (
			@PathVariable Integer organizationId,
			Model model) throws IOException {
		
		OrganizationBean organization = organizationRepository.findById(organizationId);
		Hibernate.initialize(organization.getManagers());
		model.addAttribute("organization", organization);
		
		return "admin/organization-managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/managers/add", method=RequestMethod.POST)
	public String addAOrganizationManager (
			@PathVariable Integer organizationId,
			@RequestParam(value="username") String username) throws Exception {
		
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
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.GET)
	public String createRepository (
			@PathVariable Integer organizationId,
			Model model) throws Exception {

		OrganizationBean organization = organizationRepository.findById(organizationId);
		model.addAttribute("organization", organization);
		
		model.addAttribute("repositoryBean", new RepositoryBean());
		
		return "admin/create-repository";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.POST)
	public String processCreationOfRepository (
			@PathVariable Integer organizationId,
			/*
			 * "Error" need to go AFTER "@Valid" but BEFORE "@RequestParam" 
			 * attributes, otherwise Spring will directly give 400 error with
			 * message:
			 * > Validation failed for object='XXX'. Error count: XXX
			 * rather than write that information into the "Error" class.
			 */
			@Valid RepositoryBean repository, 
			Errors errors, 
			@RequestParam(value="include_setup_files") Boolean includeSetupFiles,
			Model model) throws Exception {

		OrganizationBean organization = organizationRepository.findById(organizationId);
		
		if (errors.hasErrors()) {
			model.addAttribute("repositoryBean", repository); 
			model.addAttribute("organization", organization);
			return "admin/create-repository";
		}
		
		repository.setOrganization(organization);

		File repositoryDirectory = gitSource.getBareRepositoryDirectory(organization.getName(), repository.getName());
		
		ClassLoader classLoader = getClass().getClassLoader();
		File sampleHooksDirectory = new File(classLoader.getResource("git-server-side-hooks").getFile());
		File configFilesDirectory = new File(classLoader.getResource("config-files").getFile());
		
		/*
		 * Here makes a bare repository with one commit (include the
		 * configuration file) in it.
		 */
		if (includeSetupFiles.equals(Boolean.FALSE)) {
			GitRepository.initBare(repositoryDirectory, sampleHooksDirectory);
		}
		else {
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
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings", method=RequestMethod.GET)
	public String showRepositorySettings (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		model.addAttribute("repositoryBean", repository);
		
		return "admin/repository-settings";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings", method=RequestMethod.POST)
	public String processRepositorySettings (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@Valid RepositoryBean repositoryAfterChange, 
			Errors errors, 
			RedirectAttributes model) throws Exception {
		
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		if (errors.hasErrors()) {
			
			model.addAttribute("organization", organization);
			model.addAttribute("repository", repository);
			model.addAttribute("repositoryBean", repositoryAfterChange);
			
			return "settings/profile";
		}
		
		assert (repository.getName().equals(repositoryAfterChange.getName()));
		
		repository.setDisplayName(repositoryAfterChange.getDisplayName());
		repository.setDescription(repositoryAfterChange.getDescription());
		repository.setIsPublic(repositoryAfterChange.getIsPublic());
		repositoryRepository.saveAndFlush(repository);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/settings";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/collaborators", method=RequestMethod.GET)
	public String showRepositoryCollaborators (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryRepository.findById(repositoryId);
		Hibernate.initialize(repository.getRepositoryMemberMaps());
		OrganizationBean organization = repository.getOrganization();
		
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
			@RequestParam(value="repository_member_map_id") Integer repositoryMemberMapId) throws Exception {

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

package com.gitenter.capsid.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gitenter.capsid.dto.RepositoryDTO;
import com.gitenter.capsid.service.MemberService;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.RepositoryManagerService;
import com.gitenter.capsid.service.RepositoryService;
import com.gitenter.capsid.service.exception.ItemNotUniqueException;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;

@Controller
public class RepositoryManagementController {
	
	private MemberService memberService;
	private OrganizationService organizationService;
	private RepositoryService repositoryService;
	private RepositoryManagerService repositoryManagerService;

	@Autowired
	public RepositoryManagementController(
			MemberService memberService, 
			OrganizationService organizationService,
			RepositoryService repositoryService,
			RepositoryManagerService repositoryManagerService) {
		
		this.memberService = memberService;
		this.organizationService = organizationService;
		this.repositoryService = repositoryService;
		this.repositoryManagerService = repositoryManagerService;
	}

	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.GET)
	public String showCreateRepositoryForm(
			@PathVariable Integer organizationId,
			Model model) throws Exception {

		OrganizationBean organization = organizationService.getOrganization(organizationId);
		model.addAttribute("organization", organization);
		
		model.addAttribute("repositoryDTO", new RepositoryDTO());
		
		return "repository-management/create";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.POST)
	public String processCreationOfRepository(
			@PathVariable Integer organizationId,
			/*
			 * "Error" need to go AFTER "@Valid" but BEFORE "@RequestParam" 
			 * attributes, otherwise Spring will directly give 400 error with
			 * message:
			 * > Validation failed for object='XXX'. Error count: XXX
			 * rather than write that information into the "Error" class.
			 */
			@Valid RepositoryDTO repositoryDTO, 
			Errors errors, 
			@RequestParam(value="include_setup_files") Boolean includeSetupFiles,
			Model model,
			Authentication authentication) throws Exception {

		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		if (errors.hasErrors()) {
			model.addAttribute("repositoryDTO", repositoryDTO); 
			model.addAttribute("organization", organization);
			return "repository-management/create";
		}
		
		try {
			MemberBean me = memberService.getMe(authentication);
			repositoryManagerService.createRepository(me, organization, repositoryDTO, includeSetupFiles);
		}
		catch(ItemNotUniqueException e) {
			model.addAttribute("repositoryDTO", repositoryDTO); 
			model.addAttribute("organization", organization);
			e.addToErrors(errors);
			return "repository-management/create";
		}

		return "redirect:/organizations/"+organizationId;
	}

	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings", method=RequestMethod.GET)
	public String showRepositorySettings(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		
		return "repository-management/settings";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/profile", method=RequestMethod.GET)
	public String showRepositoryProfileSettingsForm(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		RepositoryDTO repositoryDTO = new RepositoryDTO();
		repositoryDTO.fillFromBean(repository);
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		model.addAttribute("repositoryDTO", repositoryDTO);
		
		return "repository-management/profile";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/profile", method=RequestMethod.POST)
	public String updateRepositoryProfile(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@Valid RepositoryDTO repositoryDTOAfterChange, 
			Errors errors, 
			RedirectAttributes model) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		if (errors.hasErrors()) {
			
			model.addAttribute("organization", organization);
			model.addAttribute("repository", repository);
			model.addAttribute("repositoryDTO", repositoryDTOAfterChange);
			
			return "repository-management/profile";
		}
		
		assert (repository.getName().equals(repositoryDTOAfterChange.getName()));
		repositoryManagerService.updateRepository(repository, repositoryDTOAfterChange);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/settings/profile";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/collaborators", method=RequestMethod.GET)
	public String showRepositoryCollaboratorsManagementPage(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model,
			Authentication authentication) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		model.addAttribute("operatorUsername", authentication.getName());
		
		model.addAttribute("collaboratorRoles", RepositoryMemberRole.collaboratorRoles());
		
		return "repository-management/collaborators";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/collaborators/add", method=RequestMethod.POST)
	public String addARepositoryCollaborator(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@RequestParam(value="to_be_add_username") String username,
			String roleName) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		MemberBean collaborator = memberService.getMemberByUsername(username);
		/*
		 * TODO:
		 * Catch the errors and redirect to the original page, if the collaborator manager 
		 * username is invalid.
		 * Right not it raises `*NotExistException` and catched by "Not Found" page which is
		 * not intuitive.
		 */
		
		repositoryManagerService.addCollaborator(repository, collaborator, roleName);
		
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/settings/collaborators";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/collaborators/remove", method=RequestMethod.POST)
	public String removeARepositoryCollaborator(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@RequestParam(value="to_be_remove_username") String toBeRemovedUsername,
			@RequestParam(value="repository_member_map_id") Integer repositoryMemberMapId) throws Exception {
		
		/*
		 * `toBeRemovedUsername` is not currently in use for the actual logic.
		 * 
		 * It is for:
		 * (1) Visibility while showing the form (since display name may collapse).
		 * (2) For selenium tests.
		 */
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		
		repositoryManagerService.removeCollaborator(repository, repositoryMemberMapId);

		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/settings/collaborators";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/delete", method=RequestMethod.GET)
	public String showDeleteRepositoryPage(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		
		return "repository-management/delete";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/delete", method=RequestMethod.POST)
	public String processDeleteRepository(
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@RequestParam(value="copy_repository_name") String copyRepositoryName,
			RedirectAttributes model) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		
		if (repository.getName().equals(copyRepositoryName)) {
			repositoryManagerService.deleteRepository(repository);
			
			/*
			 * TODO:
			 * Message for successful delete organization.
			 */
			return "redirect:/organizations/"+organizationId;
		}
		else {
			model.addFlashAttribute("errorMessage", "Repository name doesn't match!");
			return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/settings/delete";
		}
	}
}

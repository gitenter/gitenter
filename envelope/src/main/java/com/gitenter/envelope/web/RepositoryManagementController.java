package com.gitenter.envelope.web;

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

import com.gitenter.envelope.dto.RepositoryDTO;
import com.gitenter.envelope.service.MemberService;
import com.gitenter.envelope.service.OrganizationManagerService;
import com.gitenter.envelope.service.OrganizationService;
import com.gitenter.envelope.service.RepositoryManagerService;
import com.gitenter.envelope.service.RepositoryService;
import com.gitenter.protease.domain.auth.MemberBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.RepositoryMemberRole;

@Controller
public class RepositoryManagementController {
	
	@Autowired MemberService memberService;
	@Autowired OrganizationService organizationService;
	@Autowired OrganizationManagerService organizationManagerService;
	@Autowired RepositoryService repositoryService;
	@Autowired RepositoryManagerService repositoryManagerService;

	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.GET)
	public String showCreateRepositoryForm (
			@PathVariable Integer organizationId,
			Model model) throws Exception {

		OrganizationBean organization = organizationService.getOrganization(organizationId);
		model.addAttribute("organization", organization);
		
		model.addAttribute("repositoryDTO", new RepositoryDTO());
		
		return "repository-management/create";
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
		
		/*
		 * TODO:
		 * "createRepository()" includes setup a folder structure under the local git folder.
		 * However, Spring(?) turns to be too smart that it caches the OS folder operation so
		 * the second time it will not trigger the real OS mkdir.
		 * 
		 * In my selenium tests, I empty the local git folder structure and run another test.
		 * However, the above optimization makes me to need to restart my server to make the
		 * mkdir actual happen in the second run. Wonder whether there's a better way to solve
		 * this problem. 
		 */
		repositoryManagerService.createRepository(authentication, organization, repositoryDTO, includeSetupFiles);

		return "redirect:/organizations/"+organizationId;
	}

	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings", method=RequestMethod.GET)
	public String showRepositorySettings (
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
	public String showRepositoryProfileSettingsForm (
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
	public String updateRepositoryProfile (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@Valid RepositoryDTO repositoryDTOAfterChange, 
			Errors errors, 
			RedirectAttributes model,
			Authentication authentication) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		if (errors.hasErrors()) {
			
			model.addAttribute("organization", organization);
			model.addAttribute("repository", repository);
			model.addAttribute("repositoryDTO", repositoryDTOAfterChange);
			
			return "repository-management/profile";
		}
		
		assert (repository.getName().equals(repositoryDTOAfterChange.getName()));
		repositoryManagerService.updateRepository(authentication, repository, repositoryDTOAfterChange);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/settings/profile";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/collaborators", method=RequestMethod.GET)
	public String showRepositoryCollaborators (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			Model model) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepositoryWithCollaborators(repositoryId);
		OrganizationBean organization = repository.getOrganization();
		
		model.addAttribute("organization", organization);
		model.addAttribute("repository", repository);
		
		model.addAttribute("collaboratorRoles", RepositoryMemberRole.collaboratorRoles());
		
		return "repository-management/collaborators";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/{repositoryId}/settings/collaborators/add", method=RequestMethod.POST)
	public String addARepositoryCollaborator (
			@PathVariable Integer organizationId,
			@PathVariable Integer repositoryId,
			@RequestParam(value="username") String username,
			String roleName,
			Authentication authentication) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		MemberBean collaborator = memberService.getMemberByUsername(username);
		/*
		 * TODO:
		 * Catch the errors and redirect to the original page,
		 * if the collaborator manager username is invalid.
		 */
		
		repositoryManagerService.addCollaborator(authentication, repository, collaborator, roleName);
		
		return "redirect:/organizations/"+organizationId+"/repositories/"+repositoryId+"/settings/collaborators";
	}
}

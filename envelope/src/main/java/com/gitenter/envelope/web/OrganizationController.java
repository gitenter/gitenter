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

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.envelope.dto.RepositoryDTO;
import com.gitenter.envelope.service.MemberService;
import com.gitenter.envelope.service.OrganizationManagerService;
import com.gitenter.envelope.service.OrganizationService;
import com.gitenter.protease.domain.auth.OrganizationBean;

@Controller
public class OrganizationController {
	
	@Autowired OrganizationService organizationService;
	
	@Autowired MemberService memberService;
	@Autowired OrganizationManagerService organizationManagerService;
	
	@RequestMapping(value="/organizations/{organizationId}", method=RequestMethod.GET)
	public String showOrganizationPage (
			@PathVariable Integer organizationId,
			Authentication authentication,
			Model model) {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		model.addAttribute("repositories", organizationService.getVisibleRepositories(organizationId, authentication));
		model.addAttribute("members", organizationService.getAllMembers(organizationId));

		return "index/organization";
	}

	@RequestMapping(value="/organizations/create", method=RequestMethod.GET)
	public String showCreateOrganizationForm (Model model) {

		model.addAttribute("organizationDTO", new OrganizationDTO());
		return "admin/create-organization";
	}
	
	@RequestMapping(value="/organizations/create", method=RequestMethod.POST)
	public String processCreationOfOrganization (
			@Valid OrganizationDTO organizationDTO, 
			Errors errors, 
			Model model,
			Authentication authentication) throws Exception {
		
		if (errors.hasErrors()) {
			model.addAttribute("organizationDTO", organizationDTO); 
			return "admin/create-organization";
		}
		
		memberService.createOrganization(authentication.getName(), organizationDTO);
		return "redirect:/";
	}

	@RequestMapping(value="/organizations/{organizationId}/settings", method=RequestMethod.GET)
	public String manageOrganizationManagers (
			@PathVariable Integer organizationId,
			Model model) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		
		return "organization-management/settings";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/members", method=RequestMethod.GET)
	public String showMemberManagementForm (
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		model.addAttribute("members", organizationService.getAllMembers(organizationId));
		model.addAttribute("myUsername", authentication.getName());
		
		return "organization-management/members";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/members/add", method=RequestMethod.POST)
	public String addAMemberToOrganization (
			@PathVariable Integer organizationId,
			@RequestParam(value="username") String username) throws Exception {
		
		organizationManagerService.addOrganizationMember(organizationId, username);		
		/*
		 * TODO:
		 * Raise errors and redirect to the original page,
		 * if the manager username is invalid.
		 */
		
		return "redirect:/organizations/"+organizationId+"/settings/members";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/managers", method=RequestMethod.GET)
	public String showManagerManagementForm (
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		model.addAttribute("managers", organizationService.getManagers(organizationId));
		model.addAttribute("myUsername", authentication.getName());
		
		return "organization-management/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/repositories/create", method=RequestMethod.GET)
	public String showCreateRepositoryForm (
			@PathVariable Integer organizationId,
			Model model) throws Exception {

		OrganizationBean organization = organizationService.getOrganization(organizationId);
		model.addAttribute("organization", organization);
		
		model.addAttribute("repositoryDTO", new RepositoryDTO());
		
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
			@Valid RepositoryDTO repositoryDTO, 
			Errors errors, 
			@RequestParam(value="include_setup_files") Boolean includeSetupFiles,
			Model model,
			Authentication authentication) throws Exception {

		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		if (errors.hasErrors()) {
			model.addAttribute("repositoryDTO", repositoryDTO); 
			model.addAttribute("organization", organization);
			return "admin/create-repository";
		}
		
		organizationManagerService.createRepository(authentication, organizationId, repositoryDTO, includeSetupFiles);

		return "redirect:/organizations/"+organizationId;
	}
}

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

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.envelope.service.MemberService;
import com.gitenter.envelope.service.OrganizationManagerService;
import com.gitenter.envelope.service.OrganizationService;
import com.gitenter.protease.domain.auth.OrganizationBean;

@Controller
public class OrganizationManagementController {
	
	@Autowired OrganizationService organizationService;
	
	@Autowired MemberService memberService;
	@Autowired OrganizationManagerService organizationManagerService;

	@RequestMapping(value="/organizations/create", method=RequestMethod.GET)
	public String showCreateOrganizationForm (Model model) {

		model.addAttribute("organizationDTO", new OrganizationDTO());
		return "organization-management/create";
	}
	
	@RequestMapping(value="/organizations/create", method=RequestMethod.POST)
	public String processCreationOfOrganization (
			@Valid OrganizationDTO organizationDTO, 
			Errors errors, 
			Model model,
			Authentication authentication) throws Exception {
		
		if (errors.hasErrors()) {
			model.addAttribute("organizationDTO", organizationDTO); 
			return "organization-management/create";
		}
		
		memberService.createOrganization(authentication, organizationDTO);
		return "redirect:/";
	}

	@RequestMapping(value="/organizations/{organizationId}/settings", method=RequestMethod.GET)
	public String showOrganizationSettings (
			@PathVariable Integer organizationId,
			Model model) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		
		return "organization-management/settings";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/profile", method=RequestMethod.GET)
	public String showOrganizationProfileSettingsForm (
			@PathVariable Integer organizationId,
			Model model) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		OrganizationDTO organizationDTO = new OrganizationDTO();
		organizationDTO.fillFromBean(organization);
		
		model.addAttribute("organization", organization);
		model.addAttribute("organizationDTO", organizationDTO);
		
		return "organization-management/profile";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/profile", method=RequestMethod.POST)
	public String updateOrganizationProfile (
			@PathVariable Integer organizationId,
			@Valid OrganizationDTO organizationDTOAfterChange, 
			Errors errors, 
			RedirectAttributes model,
			Authentication authentication) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		if (errors.hasErrors()) {
			
			model.addAttribute("organization", organization);
			model.addAttribute("organizationDTO", organizationDTOAfterChange);
			
			return "organization-management/profile";
		}
		
		assert (organization.getName().equals(organizationDTOAfterChange.getName()));
		organizationManagerService.updateOrganization(authentication, organization, organizationDTOAfterChange);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/organizations/"+organizationId+"/settings/profile";
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
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.addOrganizationMember(organization, username);		
		/*
		 * TODO:
		 * Raise errors and redirect to the original page,
		 * if the manager username is invalid.
		 */
		
		return "redirect:/organizations/"+organizationId+"/settings/members";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/members/remove", method=RequestMethod.POST)
	public String removeAMemberToOrganization (
			@PathVariable Integer organizationId,
			@RequestParam(value="username") String username) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.removeOrganizationMember(organization, username);		
		
		return "redirect:/organizations/"+organizationId+"/settings/members";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/managers", method=RequestMethod.GET)
	public String showManagerManagementForm (
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		model.addAttribute("managers", organizationService.getManagers(organizationId));
		model.addAttribute("ordinaryMembers", organizationService.getOrdinaryMembers(organizationId));
		model.addAttribute("myUsername", authentication.getName());
		
		return "organization-management/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/managers/add", method=RequestMethod.POST)
	public String addAMamangerToOrganization (
			@PathVariable Integer organizationId,
			@RequestParam(value="username") String username) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.addOrganizationManager(organization, username);		
		
		return "redirect:/organizations/"+organizationId+"/settings/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/managers/remove", method=RequestMethod.POST)
	public String removeAMamangerToOrganization (
			@PathVariable Integer organizationId,
			@RequestParam(value="username") String username,
			Authentication authentication) throws Exception {
		
		/*
		 * The reason to use "username" rather than member ID as the input to
		 * add/remove managers:
		 * 
		 * (1) Same interface as add member to organization.
		 * (2) Makes compare to authentication (manager cannot remove himself/herself)
		 * easier.
		 */
		if (!username.equals(authentication.getName())) {
			OrganizationBean organization = organizationService.getOrganization(organizationId);
			organizationManagerService.removeOrganizationManager(organization, username);	
		}
		
		return "redirect:/organizations/"+organizationId+"/settings/managers";
	}
}

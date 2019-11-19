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

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.service.UserService;
import com.gitenter.capsid.service.OrganizationManagerService;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.exception.ItemNotUniqueException;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.OrganizationBean;

@Controller
public class OrganizationManagementController {
	
	private UserService userService;
	private OrganizationService organizationService;
	private OrganizationManagerService organizationManagerService;

	@Autowired
	public OrganizationManagementController(
			UserService userService, 
			OrganizationService organizationService,
			OrganizationManagerService organizationManagerService) {

		this.userService = userService;
		this.organizationService = organizationService;
		this.organizationManagerService = organizationManagerService;
	}
	
	@RequestMapping(value="/organizations/create", method=RequestMethod.GET)
	public String showCreateOrganizationForm(Model model) {

		model.addAttribute("organizationDTO", new OrganizationDTO());
		return "organization-management/create";
	}
	
	@RequestMapping(value="/organizations/create", method=RequestMethod.POST)
	public String processCreationOfOrganization(
			@Valid OrganizationDTO organizationDTO, 
			Errors errors, 
			Model model,
			Authentication authentication) throws Exception {
		
		if (errors.hasErrors()) {
			model.addAttribute("organizationDTO", organizationDTO);
			return "organization-management/create";
		}
		
		try {
			UserBean me = userService.getMe(authentication);
			organizationManagerService.createOrganization(me, organizationDTO);
		}
		catch(ItemNotUniqueException e) {
			model.addAttribute("organizationDTO", organizationDTO);
			e.addToErrors(errors);
			return "organization-management/create";
		}
		return "redirect:/";
	}

	@RequestMapping(value="/organizations/{organizationId}/settings", method=RequestMethod.GET)
	public String showOrganizationSettings(
			@PathVariable Integer organizationId,
			Model model) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		
		return "organization-management/settings";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/profile", method=RequestMethod.GET)
	public String showOrganizationProfileSettingsForm(
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
	public String updateOrganizationProfile(
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

		organizationManagerService.updateOrganization(authentication, organization, organizationDTOAfterChange);
		
		model.addFlashAttribute("successfulMessage", "Changes has been saved successfully!");
		return "redirect:/organizations/"+organizationId+"/settings/profile";
	}
	
	/*
	 * TODO:
	 * 
	 * There is a lot of duplicated code for 
	 * (1) add/remove/switch role of users in here
	 * (2) add/remove/switch role of collaborator in repository management
	 * 
	 * both in the controller and in the view layer (potentially also in
	 * the service layer.
	 * 
	 * Wonder if there's a way to make a general framework which can handle
	 * the two all together?
	 */
	@RequestMapping(value="/organizations/{organizationId}/settings/members", method=RequestMethod.GET)
	public String showMemberManagementPage(
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		model.addAttribute("operatorUsername", authentication.getName());
		
		return "organization-management/members";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/members/add", method=RequestMethod.POST)
	public String addAUserToOrganization(
			@PathVariable Integer organizationId,
			@RequestParam(value="to_be_add_username") String username) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		UserBean toBeAddUser = userService.getUserByUsername(username);
		organizationManagerService.addOrganizationMember(organization, toBeAddUser);		
		/*
		 * TODO:
		 * Raise errors and redirect to the original page,
		 * if the manager username is invalid.
		 */
		
		return "redirect:/organizations/"+organizationId+"/settings/members";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/members/remove", method=RequestMethod.POST)
	public String removeAUserToOrganization(
			@PathVariable Integer organizationId,
			@RequestParam(value="to_be_remove_username") String username,
			@RequestParam(value="organization_user_map_id") Integer organizationUserMapId) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.removeOrganizationMember(organization, organizationUserMapId);		
		
		return "redirect:/organizations/"+organizationId+"/settings/members";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/managers", method=RequestMethod.GET)
	public String showManagerManagementPage(
			@PathVariable Integer organizationId,
			Model model,
			Authentication authentication) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		model.addAttribute("managerMaps", organizationService.getManagerMaps(organizationId));
		model.addAttribute("memberMaps", organizationService.getMemberMaps(organizationId));
		model.addAttribute("operatorUsername", authentication.getName());
		
		return "organization-management/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/managers/add", method=RequestMethod.POST)
	public String addAMamangerToOrganization(
			@PathVariable Integer organizationId,
			@RequestParam(value="to_be_upgrade_username") String username,
			@RequestParam(value="organization_user_map_id") Integer organizationUserMapId) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.addOrganizationManager(organization, organizationUserMapId);		
		
		return "redirect:/organizations/"+organizationId+"/settings/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/managers/remove", method=RequestMethod.POST)
	public String removeAMamangerToOrganization(
			@PathVariable Integer organizationId,
			@RequestParam(value="to_be_downgrade_username") String username,
			@RequestParam(value="organization_user_map_id") Integer organizationUserMapId,
			Authentication authentication) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.removeOrganizationManager(authentication, organization, organizationUserMapId);	
		
		return "redirect:/organizations/"+organizationId+"/settings/managers";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/delete", method=RequestMethod.GET)
	public String showDeleteOrganizationPage(
			@PathVariable Integer organizationId,
			Model model) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		model.addAttribute("organization", organization);
		
		return "organization-management/delete";
	}
	
	@RequestMapping(value="/organizations/{organizationId}/settings/delete", method=RequestMethod.POST)
	public String processDeleteOrganization(
			@PathVariable Integer organizationId,
			@RequestParam(value="copy_organization_name") String copyOrganizationName,
			RedirectAttributes model) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		if (organization.getName().equals(copyOrganizationName)) {
			organizationManagerService.deleteOrganization(organization);
			
			/*
			 * TODO:
			 * Message for successful delete organization.
			 */
			return "redirect:/";
		}
		else {
			model.addFlashAttribute("errorMessage", "Organization name doesn't match!");
			return "redirect:/organizations/"+organizationId+"/settings/delete";
		}
	}
}

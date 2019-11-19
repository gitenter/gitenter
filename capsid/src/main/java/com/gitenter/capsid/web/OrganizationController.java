package com.gitenter.capsid.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gitenter.capsid.service.OrganizationService;

@Controller
public class OrganizationController {
	
	@Autowired OrganizationService organizationService;
	
	@RequestMapping(value="/organizations/{organizationId}", method=RequestMethod.GET)
	public String showOrganizationPage(
			@PathVariable Integer organizationId,
			Authentication authentication,
			Model model) throws Exception {
		
		model.addAttribute("organization", organizationService.getOrganization(organizationId));
		model.addAttribute("repositories", organizationService.getVisibleRepositories(organizationId, authentication));
		model.addAttribute("members", organizationService.getAllPersons(organizationId));

		return "index/organization";
	}
}

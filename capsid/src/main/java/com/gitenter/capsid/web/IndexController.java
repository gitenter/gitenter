package com.gitenter.capsid.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gitenter.capsid.service.PersonService;

@Controller
public class IndexController {
	
	@Autowired PersonService personService;

	@RequestMapping("/")
	public String showMainPage(Model model, Authentication authentication) throws Exception {
		
		String username = authentication.getName();
		
		/*
		 * The other possibility is just get member, and do the loops
		 * (even with condition to check role) inside of the presentation
		 * layer. I am not doing it, to hide the implementation detail
		 * of member roles (by the database/domain layer) inside of 
		 * the service layer.
		 */
		model.addAttribute("managedOrganizations", personService.getManagedOrganizations(username));
		model.addAttribute("belongedOrganizations", personService.getBelongedOrganizations(username));
		model.addAttribute("organizedRepositories", personService.getOrganizedRepositories(username));
		model.addAttribute("authoredRepositories", personService.getAuthoredRepositories(username));

		return "index/main";
	}
}

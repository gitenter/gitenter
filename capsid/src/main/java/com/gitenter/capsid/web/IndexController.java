package com.gitenter.capsid.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gitenter.capsid.service.UserService;

@Controller
public class IndexController {
	
	@Autowired UserService userService;

	@RequestMapping("/")
	public String showMainPage(Model model, Authentication authentication) throws Exception {
		
		String username = authentication.getName();
		
		/*
		 * The other possibility is just get user, and do the loops
		 * (even with condition to check role) inside of the presentation
		 * layer. I am not doing it, to hide the implementation detail
		 * of user roles (by the database/domain layer) inside of 
		 * the service layer.
		 */
		model.addAttribute("managedOrganizations", userService.getManagedOrganizations(username));
		model.addAttribute("belongedOrganizations", userService.getBelongedOrganizations(username));
		model.addAttribute("organizedRepositories", userService.getOrganizedRepositories(username));
		model.addAttribute("authoredRepositories", userService.getAuthoredRepositories(username));

		return "index/main";
	}
}

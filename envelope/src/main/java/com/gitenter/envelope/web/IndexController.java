package com.gitenter.envelope.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gitenter.envelope.service.MemberService;

@Controller
public class IndexController {
	
	@Autowired MemberService memberService;

	@RequestMapping("/")
	public String main(Model model, Authentication authentication) throws Exception {
		
		String username = authentication.getName();
		
		/*
		 * The other possibility is just get member, and do the loops
		 * (even with condition to check role) inside of the presentation
		 * layer. I am not doing it, to hide the implementation detail
		 * of member roles (by the database/domain layer) inside of 
		 * the service layer.
		 */
		model.addAttribute("managedOrganizations", memberService.getManagedOrganizations(username));
		model.addAttribute("accessibleOrganizations", memberService.getAccessibleOrganizations(username));
		model.addAttribute("organizedRepositories", memberService.getOrganizedRepositories(username));
		model.addAttribute("editableRepositories", memberService.getEditableRepositories(username));

		return "index/main";
	}
}

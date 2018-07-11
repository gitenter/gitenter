package com.gitenter.envelope.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gitenter.envelope.dto.OrganizationDTO;
import com.gitenter.envelope.service.MemberService;

@Controller
public class OrganizationController {
	
	@Autowired MemberService memberService;

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
}

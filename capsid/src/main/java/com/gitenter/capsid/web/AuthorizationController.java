package com.gitenter.capsid.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gitenter.capsid.dto.LoginDTO;
import com.gitenter.capsid.dto.MemberRegisterDTO;
import com.gitenter.capsid.service.AnonymousService;

@Controller
public class AuthorizationController {

	@Autowired private AnonymousService anonymousService;

	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegisterForm (Model model) {
		
		/* 
		 * The modelAttribute NEED to be the same as the class name,
		 * otherwise the <sf:errors> will not render. 
		 */
		model.addAttribute("memberRegisterDTO", new MemberRegisterDTO());
		return "authorization/register";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegistration (
			/* 
			 * Use "ModelAttribute" rather than directly put the value into model,
			 * otherwise the <sf:errors> will not render. 
			 */
			@ModelAttribute("memberRegisterDTO") @Valid MemberRegisterDTO memberRegisterDTO, 
			Errors errors) {
		
		if (errors.hasErrors()) {
			return "authorization/register";
		}
		
		anonymousService.signUp(memberRegisterDTO);
		return "redirect:/login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLoginForm (
			Model model,
			@RequestParam(value="error", required=false) String error) {
		
		if (error != null) {
			model.addAttribute("message", "Invalid username and password!");
		}

		model.addAttribute("loginDTO", new LoginDTO());
		return "authorization/login";
	}
}

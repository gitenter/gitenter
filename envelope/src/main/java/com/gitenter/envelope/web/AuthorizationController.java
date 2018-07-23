package com.gitenter.envelope.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gitenter.envelope.dto.LoginDTO;
import com.gitenter.envelope.dto.MemberRegisterDTO;
import com.gitenter.envelope.service.AnonymousService;

@Controller
public class AuthorizationController {

	@Autowired private AnonymousService anonymousService;

	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegisterForm (Model model) {
		
		/* 
		 * The modelAttribute NEED to be the same as the class name,
		 * otherwise the <sf:errors> will not render. 
		 */
		model.addAttribute("signUpDTO", new MemberRegisterDTO());
		return "authorization/register";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegistration (
			@Valid MemberRegisterDTO signUpDTO, 
			Errors errors, 
			Model model) {
		
		if (errors.hasErrors()) {
			/* 
			 * So <sf:> will render the values in object "member" to the form.
			 */
			model.addAttribute("memberDTO", signUpDTO); 
			return "authorization/register";
		}
		
		anonymousService.signUp(signUpDTO);
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

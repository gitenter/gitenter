package com.gitenter.capsid.web;

import javax.servlet.http.HttpServletRequest;
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
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.AnonymousService;
import com.gitenter.capsid.service.exception.ItemNotUniqueException;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class AuthorizationController {
	
	private final AnonymousService anonymousService;

	@Autowired
	public AuthorizationController(AnonymousService anonymousService) {
		this.anonymousService = anonymousService;
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String showRegisterForm(Model model) {
		
		/* 
		 * The modelAttribute NEED to be the same as the class name,
		 * otherwise the <sf:errors> will not render. 
		 */
		model.addAttribute("userRegisterDTO", new UserRegisterDTO());
		return "authorization/register";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String processRegistration(
			/* 
			 * Use "ModelAttribute" rather than directly put the value into model,
			 * otherwise the <sf:errors> will not render. 
			 */
			@ModelAttribute("userRegisterDTO") @Valid UserRegisterDTO userRegisterDTO, 
			Errors errors,
			Model model,
			HttpServletRequest request) throws Exception {
		
		log.debug("User registration attempt: "+userRegisterDTO);
		
		if (errors.hasErrors()) {
			System.out.println(errors.getFieldErrors());
			return "authorization/register";
		}
		
		try {
			anonymousService.signUp(userRegisterDTO);
			log.info("User registered: "+userRegisterDTO+". IP: "+request.getRemoteAddr());
		}
		catch(ItemNotUniqueException e) {
			e.addToErrors(errors);
			return "authorization/register";
		}
		
		/*
		 * TODO:
		 * Should reply some kind of "register successful", rather than directly go back to
		 * the login page.
		 */
		return "redirect:/login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String showLoginForm(
			Model model,
			@RequestParam(value="error", required=false) String error) {
		
		if (error != null) {
			model.addAttribute("message", "Invalid username and password!");
		}

		model.addAttribute("loginDTO", new LoginDTO());
		return "authorization/login";
	}
}

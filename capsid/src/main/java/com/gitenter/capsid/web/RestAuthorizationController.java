package com.gitenter.capsid.web;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.dto.LoginDTO;
import com.gitenter.capsid.dto.MemberRegisterDTO;
import com.gitenter.capsid.service.AnonymousService;

@RestController
public class RestAuthorizationController {
	
	private static final Logger logger = LoggerFactory.getLogger(RestAuthorizationController.class);
	
	private final AnonymousService anonymousService;

	@Autowired
	public RestAuthorizationController(AnonymousService anonymousService) {
		this.anonymousService = anonymousService;
	}

	/*
	 * TODO: 
	 * CORS setup globally, and only allowed for specified frontends.
	 */
	@CrossOrigin
	@RequestMapping(value="/register", method=RequestMethod.GET)
	@ResponseBody
	public MemberRegisterDTO showRegisterForm(Model model) {
		return new MemberRegisterDTO();
	}
	
	@CrossOrigin
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@ResponseBody
	public MemberRegisterDTO processRegistration(
			@RequestBody @Valid MemberRegisterDTO memberRegisterDTO) throws Exception {
		
		logger.debug("User registration attempt: "+memberRegisterDTO);
		anonymousService.signUp(memberRegisterDTO);
		
		return memberRegisterDTO;
	}
	
	@RequestMapping(value="/restlogin", method=RequestMethod.GET)
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

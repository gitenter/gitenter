package com.gitenter.capsid.api;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.AnonymousService;
import com.gitenter.protease.domain.auth.UserBean;

@RestController
@RequestMapping(value="/api")
public class RestAuthorizationController {
	
	private static final Logger logger = LoggerFactory.getLogger(RestAuthorizationController.class);
	
	@Autowired private AnonymousService anonymousService;

	/*
	 * TODO: 
	 * CORS setup globally, and only allowed for specified frontends.
	 */
	@CrossOrigin
	@RequestMapping(value="/register", method=RequestMethod.GET)
	@ResponseBody
	public UserRegisterDTO showRegisterForm() {
		return new UserRegisterDTO();
	}
	
	@CrossOrigin
	@RequestMapping(value="/register", method=RequestMethod.POST)
	@ResponseBody
	public UserBean processRegistration(
			@RequestBody @Valid UserRegisterDTO userRegisterDTO) throws Exception {
		
		logger.debug("User registration attempt: "+userRegisterDTO);
		UserBean userBean = anonymousService.signUp(userRegisterDTO);
		
		return userBean;
	}
}

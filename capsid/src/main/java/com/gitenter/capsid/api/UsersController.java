package com.gitenter.capsid.api;

import java.io.IOException;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.AnonymousService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.UserBean;

@RestController
@RequestMapping(value="/api")
public class UsersController {
	
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
	
	@Autowired private AnonymousService anonymousService;
	@Autowired UserService userService;
	
	/*
	 * TODO:
	 * Consider removing user creation/register out of the API (so programmable web
	 * cannot do it while only human can do). Also introduce CAPTCHA graphic validation
	 * in the user creation step.
	 * 
	 * TODO:
	 * Right now raises error but will return 200.
	 * > ERROR o.h.e.jdbc.spi.SqlExceptionHelper - ERROR: duplicate key value violates unique constraint "application_user_username_key"
  	 * > Detail: Key (username)=(integration_test) already exists.
	 * It should return 409 ("conflict") if username already exist. 
	 */
	@CrossOrigin
	@RequestMapping(value="/users", method=RequestMethod.POST)
	@ResponseBody
	public UserBean processRegistration(
			@RequestBody @Valid UserRegisterDTO userRegisterDTO) throws Exception {
		
		logger.debug("User registration attempt: "+userRegisterDTO);
		UserBean userBean = anonymousService.signUp(userRegisterDTO);
		
		return userBean;
	}

	@RequestMapping(value="/users/{userId}", method=RequestMethod.GET)
	@ResponseBody
	public UserBean showUserInfo(@PathVariable @Min(1) Integer userId) throws IOException {
		return userService.getUserById(userId);
	}
}

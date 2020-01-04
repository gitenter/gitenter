package com.gitenter.capsid.api;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.dto.ChangePasswordDTO;
import com.gitenter.capsid.dto.SshKeyFieldDTO;
import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.AnonymousService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.SshKeyBean;
import com.gitenter.protease.domain.auth.UserBean;

//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value="/api/users")
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
	 * 
	 * Tried to use `ExceptionHandler` and wrap a `ErrorInfo`. It seems will override
	 * Java validation `@Valid` error output form.
	 */
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public UserBean registerUser(
			@RequestBody @Valid UserRegisterDTO userRegister) throws Exception {
		
		logger.debug("User registration attempt: "+userRegister);
		UserBean userBean = anonymousService.signUp(userRegister);
		logger.debug("User registered. Profile: "+userBean);
		
		return userBean;
	}

	@RequestMapping(value="/{userId}", method=RequestMethod.GET)
	@ResponseBody
	public UserBean getUserInfo(@PathVariable @Min(1) Integer userId) throws IOException {
		return userService.getUserById(userId);
	}
	
//	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@RequestMapping(value="/me", method=RequestMethod.GET)
	@ResponseBody
	public UserBean getMe(Authentication authentication) throws Exception {
		return userService.getMe(authentication);
	}
	
	@RequestMapping(value="/me", method=RequestMethod.PUT)
	@ResponseBody
	public UserBean updateProfile(
			@RequestBody @Valid UserProfileDTO userProfile) throws Exception {
		
		/*
		 * TODO:
		 * Exception handling.
		 */
		logger.debug("Update profile attempt: "+userProfile);
		UserBean userBean = userService.updateUser(userProfile);
		logger.info("User update profile. New profile: "+userBean);
		
		return userBean;
	}
	
	@RequestMapping(value="/me", method=RequestMethod.DELETE)
	@ResponseBody
	public void deleteAccount(
			Authentication authentication,
			@RequestParam(value="password") String password) throws Exception {
		
		/*
		 * TODO:
		 * Exception handling.
		 */
		logger.debug("Delete account attempt: "+authentication.getName());
		userService.deleteUser(authentication.getName(), password);
		logger.info("Account deleted: "+authentication.getName());
	}
	
	/*
	 * Password changing is not idempotent ("old password" need to be
	 * changed everytime). So use POST.
	 */
	@RequestMapping(value="/me/password", method=RequestMethod.POST)
	@ResponseBody
	public void changePassword(
			Authentication authentication,
			@RequestBody @Valid ChangePasswordDTO changePasswordDTO) throws Exception {
		
		/*
		 * TODO:
		 * Exception handling.
		 */
		logger.debug("Change password attempt: "+authentication.getName());
		userService.updatePassword(authentication, changePasswordDTO);
		logger.info("Password changed: "+authentication.getName());
	}
	
	@RequestMapping(value="/me/ssh-keys", method=RequestMethod.GET)
	@ResponseBody
	public List<SshKeyBean> getSshKeys(Authentication authentication) throws Exception {
		
		UserBean user = userService.getUserByUsername(authentication.getName());
		return user.getSshKeys();
	}
	
	@RequestMapping(value="/me/ssh-keys", method=RequestMethod.POST)
	@ResponseBody
	public void addSshKey(
			Authentication authentication,
			@RequestBody @Valid SshKeyFieldDTO sshKeyField) throws Exception {
		
		/*
		 * TODO:
		 * Exception handling.
		 */
		UserBean user = userService.getUserByUsername(authentication.getName());
		SshKeyBean sshKey = sshKeyField.toBean();
		userService.addSshKey(sshKey, user);
	}
}

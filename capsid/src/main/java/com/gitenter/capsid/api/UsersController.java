package com.gitenter.capsid.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.dto.ChangePasswordDTO;
import com.gitenter.capsid.dto.SshKeyFieldDTO;
import com.gitenter.capsid.dto.UserProfileDTO;
import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.capsid.service.AnonymousService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.SshKeyBean;
import com.gitenter.protease.domain.auth.UserBean;

import lombok.extern.slf4j.Slf4j;

//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value="/api/users")
@Slf4j
public class UsersController {
		
	@Autowired private AnonymousService anonymousService;
	@Autowired UserService userService;
	
	/*
	 * TODO:
	 * Consider removing user creation/register out of the API (so programmable web
	 * cannot do it while only human can do). Also introduce CAPTCHA graphic validation
	 * in the user creation step.
	 * 
	 * TODO:
	 * Right now raises error but will return 500.
	 * > ERROR o.h.e.jdbc.spi.SqlExceptionHelper - ERROR: duplicate key value violates unique constraint "application_user_username_key"
  	 * > Detail: Key (username)=(integration_test) already exists.
	 * It should return 409 ("conflict") if username already exist. 
	 * 
	 * Tried to use `ExceptionHandler` and wrap a `ErrorInfo`. It seems will override
	 * Java validation `@Valid` error output form.
	 */
	@PostMapping
	public EntityModel<UserBean> registerUser(
			@RequestBody @Valid UserRegisterDTO userRegister) throws Exception {
		
		log.debug("User registration attempt: "+userRegister);
		UserBean userBean = anonymousService.signUp(userRegister);
		log.debug("User registered. Profile: "+userBean);
		
		return new EntityModel<>(userBean,
				linkTo(methodOn(UsersController.class).registerUser(userRegister)).withSelfRel(),
				linkTo(methodOn(UsersController.class).getUser(userBean.getId())).withRel("user"));
	}

	@GetMapping("/{userId}")
	public EntityModel<UserBean> getUser(@PathVariable @Min(1) Integer userId) throws IOException {
		return new EntityModel<>(userService.getUserById(userId),
				linkTo(methodOn(UsersController.class).getUser(userId)).withSelfRel());
	}
	
//	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping("/me")
	public EntityModel<UserBean> getMe(Authentication authentication) throws Exception {
		UserBean userBean = userService.getMe(authentication);
		return new EntityModel<>(userBean,
				linkTo(methodOn(UsersController.class).getMe(authentication)).withSelfRel(),
				linkTo(methodOn(UsersController.class).getUser(userBean.getId())).withRel("user"));
	}
	
	@PutMapping("/me")
	public UserBean updateProfile(
			@RequestBody @Valid UserProfileDTO userProfile) throws Exception {
		
		/*
		 * TODO:
		 * Exception handling.
		 */
		log.debug("Update profile attempt: "+userProfile);
		UserBean userBean = userService.updateUser(userProfile);
		log.info("User update profile. New profile: "+userBean);
		
		return userBean;
	}
	
	@DeleteMapping("/me")
	public void deleteAccount(
			Authentication authentication,
			@RequestParam(value="password") String password) throws Exception {
		
		/*
		 * TODO:
		 * Exception handling.
		 */
		log.debug("Delete account attempt: "+authentication.getName());
		userService.deleteUser(authentication.getName(), password);
		log.info("Account deleted: "+authentication.getName());
	}
	
	/*
	 * Password changing is not idempotent ("old password" need to be
	 * changed everytime). So use POST.
	 */
	@PostMapping("/me/password")
	public void changePassword(
			Authentication authentication,
			@RequestBody @Valid ChangePasswordDTO changePasswordDTO) throws Exception {
		
		/*
		 * TODO:
		 * Exception handling.
		 */
		log.debug("Change password attempt: "+authentication.getName());
		userService.updatePassword(authentication, changePasswordDTO);
		log.info("Password changed: "+authentication.getName());
	}
	
	@GetMapping("/me/ssh-keys")
	public List<SshKeyBean> getSshKeys(Authentication authentication) throws Exception {
		
		UserBean user = userService.getUserByUsername(authentication.getName());
		return user.getSshKeys();
	}
	
	@PostMapping("/me/ssh-keys")
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

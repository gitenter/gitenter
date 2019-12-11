package com.gitenter.capsid.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.UserBean;

@RestController
@RequestMapping(value="/api")
public class RestUserController {
	
	@Autowired UserService userService;

	@RequestMapping(value="/users/{userId}", method=RequestMethod.GET)
	@ResponseBody
	public UserBean showUserInfo(@PathVariable Integer userId) throws IOException {
		return userService.getUserById(userId);
	}
}

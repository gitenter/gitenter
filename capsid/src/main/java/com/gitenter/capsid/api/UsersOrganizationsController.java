package com.gitenter.capsid.api;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.OrganizationBean;

@RestController
@RequestMapping(value="/api/users")
public class UsersOrganizationsController {
	
	@Autowired UserService userService;

	@GetMapping("/me/organizations")
	public List<OrganizationBean> showOrganization(
			Authentication authentication,
			@RequestParam("role") String role) throws IOException {

		switch (role) {
		case "manager":
			return userService.getManagedOrganizations(authentication.getName());
		case "ordinary_member":
			return userService.getBelongedOrganizations(authentication.getName());
		default:
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, 
					"Request pamameter 'role' should have value 'manager' or 'ordinary_member'");
		}
	}
}
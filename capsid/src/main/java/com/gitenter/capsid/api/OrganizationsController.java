package com.gitenter.capsid.api;

import java.io.IOException;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.protease.domain.auth.OrganizationBean;

@RestController
@RequestMapping(value="/api")
public class OrganizationsController {
	
	@Autowired OrganizationService organizationService;

	@GetMapping("/organizations/{organizationId}")
	public OrganizationBean showOrganization(@PathVariable @Min(1) Integer organizationId) throws IOException {
		return organizationService.getOrganization(organizationId);
	}
}

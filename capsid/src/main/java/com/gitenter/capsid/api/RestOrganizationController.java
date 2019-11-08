package com.gitenter.capsid.api;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.protease.domain.auth.OrganizationBean;

@RestController
@RequestMapping(value="/api")
public class RestOrganizationController {
	
	@Autowired OrganizationService organizationService;

	@RequestMapping(value="/organizations/{organizationId}", method=RequestMethod.GET)
	@ResponseBody
	public OrganizationBean showOrganization(@PathVariable Integer organizationId) throws IOException {
		return organizationService.getOrganization(organizationId);
	}
}

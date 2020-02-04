package com.gitenter.capsid.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.service.OrganizationManagerService;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.UserBean;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value="/api/organizations")
@Slf4j
public class OrganizationsController {
	
	@Autowired UserService userService;
	@Autowired OrganizationService organizationService;
	@Autowired private OrganizationManagerService organizationManagerService;
	
	@PostMapping
	public EntityModel<OrganizationBean> createOrganization(
			@RequestBody @Valid OrganizationDTO organizationDTO,
			Authentication authentication) throws IOException {
		
		UserBean me = userService.getMe(authentication);
		OrganizationBean organizationBean = organizationManagerService.createOrganization(me, organizationDTO);
		
		return new EntityModel<>(organizationBean,
				linkTo(methodOn(OrganizationsController.class).createOrganization(organizationDTO, authentication)).withSelfRel(),
				linkTo(methodOn(OrganizationsController.class).getOrganization(organizationBean.getId())).withRel("organization"));
	}

	@GetMapping("/{organizationId}")
	public EntityModel<OrganizationBean> getOrganization(@PathVariable @Min(1) Integer organizationId) throws IOException {
		return new EntityModel<>(organizationService.getOrganization(organizationId),
				linkTo(methodOn(OrganizationsController.class).getOrganization(organizationId)).withSelfRel());
	}

	@PutMapping("/{organizationId}")
	public EntityModel<OrganizationBean> updateOrganizationProperties(
			@PathVariable @Min(1) Integer organizationId,
			@RequestBody @Valid OrganizationDTO organizationDTOAfterChange) throws IOException {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		OrganizationBean updatedOrganization = organizationManagerService.updateOrganization(organization, organizationDTOAfterChange);
		return new EntityModel<>(updatedOrganization,
				linkTo(methodOn(OrganizationsController.class).getOrganization(organizationId)).withSelfRel());
	}
	
	@DeleteMapping("/{organizationId}")
	public void deleteOrganization(
			@PathVariable @Min(1) Integer organizationId,
			@RequestParam(value="organization_name") String organizationName) throws IOException {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		/*
		 * TODO:
		 * Move this part of the logic to controller.
		 */
		if (!organization.getName().equals(organizationName)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, 
					"Organization name doesn't match!");
		}
		
		organizationManagerService.deleteOrganization(organization);
	}
	
	@GetMapping("/{organizationId}/members")
	public List<UserBean> getAllMembers(
			@PathVariable @Min(1) Integer organizationId,
			Authentication authentication) throws Exception {
		
		log.debug("Get organization members attempt");
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		if (organizationService.isManager(organizationId, authentication)) {
			List<UserBean> users = new ArrayList<UserBean>();
			for (OrganizationUserMapBean m : organizationService.getAllMemberMaps(organization)) {
				users.add(m.getUserDetails());
			}
			return users;
		}
		else {
			return organizationService.getAllMembers(organization);
		}
	}
	
	@DeleteMapping("/{organizationId}/members/{organizationUserMapId}")
	public void removeMember(
			@PathVariable @Min(1) Integer organizationId,
			@PathVariable @Min(1) Integer organizationUserMapId,
			Authentication authentication) throws IOException {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.removeOrganizationMember(authentication, organization, organizationUserMapId);
	}
	
	@GetMapping("/{organizationId}/ordinary-members")
	public List<UserBean> getOrdinaryMembers(
			@PathVariable @Min(1) Integer organizationId,
			Authentication authentication) throws Exception {
		
		log.debug("Get organization ordinary members attempt");
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		if (organizationService.isManager(organizationId, authentication)) {
			List<UserBean> users = new ArrayList<UserBean>();
			for (OrganizationUserMapBean m : organizationService.getOrdinaryMemberMaps(organization)) {
				users.add(m.getUserDetails());
			}
			return users;
		}
		else {
			return organizationService.getAllOrdinaryMembers(organization);
		}
	}
	
	@PostMapping("/{organizationId}/ordinary-members")
	public UserBean addOrdinaryMember(
			@PathVariable @Min(1) Integer organizationId,
			@RequestParam(value="username") String username) throws Exception {
		
		log.debug("Add member attempt: Username "+username);
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		UserBean toBeAddUser = userService.getUserByUsername(username);
		log.debug("Add member attempt: User object "+toBeAddUser);
		return organizationManagerService.addOrganizationOrdinaryMember(organization, toBeAddUser).getUserDetails();
		/*
		 * TODO:
		 * Raise errors and redirect to the original page,
		 * if the manager username is invalid.
		 */
	}
	
	@GetMapping("/{organizationId}/managers")
	public List<UserBean> getManagers(
			@PathVariable @Min(1) Integer organizationId,
			Authentication authentication) throws Exception {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		if (organizationService.isManager(organizationId, authentication)) {
			List<UserBean> users = new ArrayList<UserBean>();
			for (OrganizationUserMapBean m : organizationService.getManagerMaps(organization)) {
				users.add(m.getUserDetails());
			}
			return users;
		}
		else {
			return organizationService.getAllManagers(organization);
		}
	}
	
	/*
	 * TODO:
	 * Consider change it to a PUT method. But then this method should NOT raise error
	 * if the user is ALREADY a manager of the organization.
	 * 
	 * TODO:
	 * Should it return OrganizationUserMap? Something else?
	 */
	@PostMapping("/{organizationId}/managers/{organizationUserMapId}")
	public void promoteManager(
			@PathVariable @Min(1) Integer organizationId,
			@PathVariable @Min(1) Integer organizationUserMapId) throws IOException {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.promoteOrganizationManager(organization, organizationUserMapId);
		/*
		 * TODO:
		 * Handle `UnreachableException` that the potential manager is not currently 
		 * an ordinary member of the organization.
		 */
	}
	
	@DeleteMapping("/{organizationId}/managers/{organizationUserMapId}")
	public void demoteManager(
			@PathVariable @Min(1) Integer organizationId,
			@PathVariable @Min(1) Integer organizationUserMapId,
			Authentication authentication) throws IOException {
		
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		organizationManagerService.demoteOrganizationManager(authentication, organization, organizationUserMapId);
	}
}

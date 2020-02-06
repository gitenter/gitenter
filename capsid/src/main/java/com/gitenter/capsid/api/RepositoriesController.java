package com.gitenter.capsid.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;

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

import com.gitenter.capsid.dto.RepositoryDTO;
import com.gitenter.capsid.service.OrganizationService;
import com.gitenter.capsid.service.RepositoryManagerService;
import com.gitenter.capsid.service.RepositoryService;
import com.gitenter.capsid.service.UserService;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.RepositoryBean;
import com.gitenter.protease.domain.auth.UserBean;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value="/api/organizations/{organizationId}/repositories")
@Slf4j
public class RepositoriesController {
	
	@Autowired private UserService userService;
	@Autowired private OrganizationService organizationService;
	@Autowired private RepositoryService repositoryService;
	@Autowired private RepositoryManagerService repositoryManagerService;

	@PostMapping
	public EntityModel<RepositoryBean> createRepository(
			@PathVariable @Min(1) Integer organizationId,
			@RequestBody @Valid RepositoryDTO repositoryDTO,
			Authentication authentication) throws Exception {
		
		UserBean me = userService.getMe(authentication);
		OrganizationBean organization = organizationService.getOrganization(organizationId);
		
		/*
		 * TODO:
		 * setup the correct `include_setup_file`.
		 */
		RepositoryBean repositoryBean = repositoryManagerService.createRepository(me, organization, repositoryDTO, Boolean.FALSE);
		
		return new EntityModel<>(repositoryBean,
				linkTo(methodOn(RepositoriesController.class).createRepository(organizationId, repositoryDTO, authentication)).withSelfRel(),
				linkTo(methodOn(RepositoriesController.class).getRepository(organizationId, repositoryBean.getId())).withRel("repository"));
	}
	
	@GetMapping("/{repositoryId}")
	public EntityModel<RepositoryBean> getRepository(
			@PathVariable @Min(1) Integer organizationId,
			@PathVariable @Min(1) Integer repositoryId) throws IOException {
		/*
		 * TODO:
		 * Raise error if repository doesn't belong to the input organizationId. 
		 */
		return new EntityModel<>(repositoryService.getRepository(repositoryId),
				linkTo(methodOn(RepositoriesController.class).getRepository(organizationId, repositoryId)).withSelfRel());
	}
	
	@PutMapping("/{repositoryId}")
	public EntityModel<RepositoryBean> updateRepositoryProperties(
			@PathVariable @Min(1) Integer organizationId,
			@PathVariable @Min(1) Integer repositoryId,
			@RequestBody @Valid RepositoryDTO repositoryDTOAfterChange) throws IOException {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		RepositoryBean updatedRepository = repositoryManagerService.updateRepository(repository, repositoryDTOAfterChange);
		return new EntityModel<>(updatedRepository,
				linkTo(methodOn(RepositoriesController.class).getRepository(organizationId, repositoryId)).withSelfRel());
	}
	
	@DeleteMapping("/{repositoryId}")
	public void deleteRepository(
			@PathVariable @Min(1) Integer organizationId,
			@PathVariable @Min(1) Integer repositoryId,
			@RequestParam(value="repository_name") String repositoryName) throws Exception {
		
		RepositoryBean repository = repositoryService.getRepository(repositoryId);
		
		/*
		 * TODO:
		 * Move this matching logic to service.
		 */
		if (!repository.getName().equals(repositoryName)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, 
					"Repository name doesn't match!");
		}
		
		repositoryManagerService.deleteRepository(repository);
		log.info("Repository deleted: "+repository);
	}
}

package com.gitenter.capsid.dto;

import java.io.IOException;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.capsid.service.exception.InvalidOperationException;
import com.gitenter.protease.domain.auth.RepositoryBean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RepositoryDTO implements CreateDTO<RepositoryBean>, ReadDTO<RepositoryBean>, UpdateDTO<RepositoryBean> {

	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@NotNull
	@Size(min=2, max=64)
	@Column(name="display_name")
	private String displayName;
	
	private String description;
	
	@NotNull
	private Boolean isPublic;
	
	@Override
	public void fillFromBean(RepositoryBean repositoryBean) {
		
		this.name = repositoryBean.getName();
		this.displayName = repositoryBean.getDisplayName();
		this.description = repositoryBean.getDescription();
		this.isPublic = repositoryBean.getIsPublic();
	}
	
	@Override
	public void updateBean(RepositoryBean repositoryBean) throws IOException {
		
		/*
		 * Right now we cannot easily change repository name, because otherwise 
		 * `git push` will be broken.
		 * 
		 * TODO:
		 * Probably should be push from id, so then name can be freely changed.
		 * However, that makes the git (command line) interface very awkward
		 * and hard to understand, as user actually need to interact with those
		 * ids.
		 */
		if (!repositoryBean.getName().equals(name)) {
			throw new InvalidOperationException("repository name cannot be modified.");
		}
		
		repositoryBean.setDisplayName(displayName);
		repositoryBean.setDescription(description);
		repositoryBean.setIsPublic(isPublic);
	}

	@Override
	public RepositoryBean toBean() {
		
		RepositoryBean repositoryBean = new RepositoryBean();
		
		repositoryBean.setName(name);
		repositoryBean.setDisplayName(displayName);
		repositoryBean.setDescription(description);
		repositoryBean.setIsPublic(isPublic);
		
		return repositoryBean;
	}
}

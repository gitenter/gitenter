package com.gitenter.envelope.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.protease.domain.auth.RepositoryBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepositoryDTO {

	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@NotNull
	@Size(min=2, max=64)
	@Column(name="display_name")
	private String displayName;
	
	private String description;
	
	private Boolean isPublic;
	
	public void fillFromRepositoryBean(RepositoryBean repositoryBean) {
		
		this.name = repositoryBean.getName();
		this.displayName = repositoryBean.getDisplayName();
		this.description = repositoryBean.getDescription();
		this.isPublic = repositoryBean.getIsPublic();
	}
	
	public void updateRepositoryBean (RepositoryBean repositoryBean) {
		
		repositoryBean.setName(name);
		repositoryBean.setDisplayName(displayName);
		repositoryBean.setDescription(description);
		repositoryBean.setIsPublic(isPublic);
	}
}

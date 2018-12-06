package com.gitenter.envelope.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.protease.domain.auth.RepositoryBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	
	private Boolean isPublic;
	
	@Override
	public void fillFromBean(RepositoryBean repositoryBean) {
		
		this.name = repositoryBean.getName();
		this.displayName = repositoryBean.getDisplayName();
		this.description = repositoryBean.getDescription();
		this.isPublic = repositoryBean.getIsPublic();
	}
	
	@Override
	public void updateBean (RepositoryBean repositoryBean) {
		
		assert repositoryBean.getName().equals(name);
		
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

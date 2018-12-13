package com.gitenter.capsid.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.protease.domain.auth.OrganizationBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationDTO implements CreateDTO<OrganizationBean>, ReadDTO<OrganizationBean>, UpdateDTO<OrganizationBean> {

	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@NotNull
	@Size(min=2, max=64)
	@Column(name="display_name")
	private String displayName;
	
	@Override
	public void fillFromBean(OrganizationBean organizationBean) {
		
		this.name = organizationBean.getName();
		this.displayName = organizationBean.getDisplayName();
	}
	
	@Override
	public void updateBean (OrganizationBean organizationBean) {
		
		assert organizationBean.getName().equals(name);
		
		organizationBean.setDisplayName(displayName);
	}

	@Override
	public OrganizationBean toBean() {
		
		OrganizationBean organizationBean = new OrganizationBean();
		
		organizationBean.setName(name);
		organizationBean.setDisplayName(displayName);
	
		return organizationBean;
	}
}

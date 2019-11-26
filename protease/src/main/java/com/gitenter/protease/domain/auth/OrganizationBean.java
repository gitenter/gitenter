package com.gitenter.protease.domain.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;

import com.gitenter.protease.domain.ModelBean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(schema = "auth", name = "organization")
public class OrganizationBean implements ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@NotNull
	@Size(min=2, max=64)
	@Column(name="display_name")
	private String displayName;

	@ToString.Exclude
	@OneToMany(targetEntity=RepositoryBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="organization")
	private List<RepositoryBean> repositories;

	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	@OneToMany(targetEntity=OrganizationUserMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="organization")
	@Where(clause = "role_shortname = 'G'")
	private List<OrganizationUserMapBean> organizationManagerMaps = new ArrayList<OrganizationUserMapBean>();
	
	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	@OneToMany(targetEntity=OrganizationUserMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="organization")
	@Where(clause = "role_shortname = 'M'")
	private List<OrganizationUserMapBean> organizationOrdinaryMemberMaps = new ArrayList<OrganizationUserMapBean>();
	
	public List<OrganizationUserMapBean> getUserMaps(OrganizationUserRole role) {
		switch(role) {
		case MANAGER:
			return organizationManagerMaps;
		case ORDINARY_MEMBER:
			return organizationOrdinaryMemberMaps;
		default:
			throw new RuntimeException("Unreachable enum value");
		}
	}
	
	public List<UserBean> getUsers(OrganizationUserRole role) {
		List<UserBean> items = new ArrayList<UserBean>();
		for (OrganizationUserMapBean map : getUserMaps(role)) {
			items.add(map.getUser());
		}
		return items;
	}
	
	public List<UserBean> getUsers() {
		List<UserBean> items = new ArrayList<UserBean>();
		
		items.addAll(getUsers(OrganizationUserRole.MANAGER));
		items.addAll(getUsers(OrganizationUserRole.ORDINARY_MEMBER));
		
		return items;
	}
	
	void addMap(OrganizationUserMapBean map) {
		switch(map.getRole()) {
		case MANAGER:
			organizationManagerMaps.add(map);
			return;
		case ORDINARY_MEMBER:
			organizationOrdinaryMemberMaps.add(map);
			return;
		default:
			throw new RuntimeException("Unreachable enum value");
		}
	}
	
	boolean removeMap(OrganizationUserMapBean map) {
		switch(map.getRole()) {
		case MANAGER:
			return organizationManagerMaps.remove(map);
		case ORDINARY_MEMBER:
			return organizationOrdinaryMemberMaps.remove(map);
		default:
			throw new RuntimeException("Unreachable enum value");
		}
	}
	
	public List<RepositoryBean> getRepositories(Boolean isPublic) {
		List<RepositoryBean> items = new ArrayList<RepositoryBean>();
		for (RepositoryBean repository : repositories) {
			if (repository.getIsPublic().equals(isPublic)) {
				items.add(repository);
			}
		}
		return items;
	}
	
	public void addRepository(RepositoryBean repository) {
		repository.setOrganization(this);
		repositories.add(repository);
	}
	
	/*
	 * TODO:
	 * Consider to have a JOIN query to get that.
	 */
//	public boolean isManagedBy (Integer userId) {
//		for (UserBean manager : managers) {
//			if (manager.getId().equals(userId)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	public RepositoryBean findRepositoryByName(String repositoryName) throws IOException {
	
		for (RepositoryBean repository : repositories) {
			if (repository.getName().equals(repositoryName)) {
				return repository;
			}
		}
		
		throw new IOException("Repository "+repositoryName+" doesn't exist for this organization!");
	}
}

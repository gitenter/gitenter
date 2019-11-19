package com.gitenter.protease.domain.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import com.gitenter.protease.domain.ModelBean;

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
	@OneToMany(targetEntity=OrganizationUserMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="organization")
	private List<OrganizationUserMapBean> organizationUserMaps = new ArrayList<OrganizationUserMapBean>();
	
	/*
	 * The alternative approach is to get them by a customized JOINed SQL query
	 * from `OrganizationUserMapRepository`.
	 * 
	 * Will not do it until some performance
	 * bottleneck is shown. Also, since Hibernate may optimize its query cache so
	 * this mapped relationship will not be loaded multiple times in the same HTTP
	 * query, and a in-process loop is cheaper compare to a SQL query, this may 
	 * actually have not-worse performance.
	 */
	public Collection<OrganizationUserMapBean> getUserMaps(OrganizationUserRole role) {
		Collection<OrganizationUserMapBean> items = new ArrayList<OrganizationUserMapBean>();
		for (OrganizationUserMapBean map : organizationUserMaps) {
			if (map.getRole().equals(role)) {
				items.add(map);
			}
		}
		return items;
	}
	
	public Collection<UserBean> getUsers(OrganizationUserRole role) {
		Collection<UserBean> items = new ArrayList<UserBean>();
		for (OrganizationUserMapBean map : getUserMaps(role)) {
			items.add(map.getUser());
		}
		return items;
	}
	
	public Collection<UserBean> getUsers() {
		Collection<UserBean> items = new ArrayList<UserBean>();
		for (OrganizationUserMapBean map : organizationUserMaps) {
			items.add(map.getUser());
		}
		return items;
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
	
	void addMap(OrganizationUserMapBean map) {
		organizationUserMaps.add(map);
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
//	
//	public boolean addManager (UserBean manager) {
//		return managers.add(manager);
//	}
//	
//	public boolean removeManager (Integer userId) {
//		
//		Iterator<UserBean> i = managers.iterator();
//		while (i.hasNext()) {
//			UserBean manager = i.next();
//			if (manager.getId().equals(userId)) {
//				i.remove();
//				return true;
//			}
//		}
//		
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

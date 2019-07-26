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

@Getter
@Setter
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

	@OneToMany(targetEntity=RepositoryBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="organization")
	private List<RepositoryBean> repositories;

	@OneToMany(targetEntity=OrganizationMemberMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="organization")
	private List<OrganizationMemberMapBean> organizationMemberMaps = new ArrayList<OrganizationMemberMapBean>();
	
	/*
	 * The alternative approach is to get them by a customized JOINed SQL query
	 * from `OrganizationMemberMapRepository`.
	 * 
	 * Will not do it until some performance
	 * bottleneck is shown. Also, since Hibernate may optimize its query cache so
	 * this mapped relationship will not be loaded multiple times in the same HTTP
	 * query, and a in-process loop is cheaper compare to a SQL query, this may 
	 * actually have not-worse performance.
	 */
	public Collection<OrganizationMemberMapBean> getMemberMaps(OrganizationMemberRole role) {
		Collection<OrganizationMemberMapBean> items = new ArrayList<OrganizationMemberMapBean>();
		for (OrganizationMemberMapBean map : organizationMemberMaps) {
			if (map.getRole().equals(role)) {
				items.add(map);
			}
		}
		return items;
	}
	
	public Collection<MemberBean> getMembers(OrganizationMemberRole role) {
		Collection<MemberBean> items = new ArrayList<MemberBean>();
		for (OrganizationMemberMapBean map : getMemberMaps(role)) {
			items.add(map.getMember());
		}
		return items;
	}
	
	public Collection<MemberBean> getMembers() {
		Collection<MemberBean> items = new ArrayList<MemberBean>();
		for (OrganizationMemberMapBean map : organizationMemberMaps) {
			items.add(map.getMember());
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
	
	void addMap(OrganizationMemberMapBean map) {
		organizationMemberMaps.add(map);
	}
	
	/*
	 * TODO:
	 * Consider to have a JOIN query to get that.
	 */
//	public boolean isManagedBy (Integer memberId) {
//		for (MemberBean manager : managers) {
//			if (manager.getId().equals(memberId)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	public boolean addManager (MemberBean manager) {
//		return managers.add(manager);
//	}
//	
//	public boolean removeManager (Integer memberId) {
//		
//		Iterator<MemberBean> i = managers.iterator();
//		while (i.hasNext()) {
//			MemberBean manager = i.next();
//			if (manager.getId().equals(memberId)) {
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

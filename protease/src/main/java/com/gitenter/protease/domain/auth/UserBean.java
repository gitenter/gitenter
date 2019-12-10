package com.gitenter.protease.domain.auth;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.protease.domain.ModelBean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * No need to add `@ToString` to all beans, as we need to `@ToString.Exclude`
 * all double link which will cause stack overflow error.
 */
@Getter
@Setter
@ToString
@Entity
@Table(schema = "auth", name = "application_user")
public class UserBean implements ModelBean {

	/*
	 * @GeneratedValue for automatically generate primary keys.
	 * 
	 * PostgreSQL has some problem with Hibernate for automatic 
	 * primary key generation. Basically only strategy=GenerationType.IDENTITY 
	 * works, but it has performance issues (compare to SEQUENCE) -- 
	 * not crucial for us.
	 * 
	 * If our dummy data is made by INSERT using specific primary key, 
	 * then it doesn't change the PostgreSQL's SEQUENCE so if later 
	 * we insert without primary key (or let Hibernate to insert) 
	 * that will cause ID conflict issues. But if we ALTER SEQUENCE
	 * in postgres then everything will goes fine.
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@NotNull
	@Size(min=2, max=16)
	@Column(name="username")
	private String username;
	
	/*
	 * Since this is the encoded one, there's no length constrain.
	 * Also, there is no need to use character array to make it safer.
	 */
	@ToString.Exclude
	@NotNull
	@Column(name="password")
	private String password;

	@NotNull
	@Size(min=2, max=64)
	@Column(name="display_name")
	private String displayName;
	
	@NotNull
	@Email
	@Column(name="email")
	private String email;
	
	@NotNull
	@Column(name="register_at", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date registerAt;

	@ToString.Exclude
	@OneToMany(targetEntity=OrganizationUserMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="user")
	private List<OrganizationUserMapBean> organizationUserMaps = new ArrayList<OrganizationUserMapBean>();
	
	@ToString.Exclude
	@OneToMany(targetEntity=RepositoryUserMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="user")
	private List<RepositoryUserMapBean> repositoryUserMaps = new ArrayList<RepositoryUserMapBean>();
	
	@ToString.Exclude
	@OneToMany(targetEntity=SshKeyBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="user")
	private List<SshKeyBean> sshKeys;
	
	/*
	 * If later on there is performance concerns, we can have a view for a JOIN
	 * with fixed role, and map it to a OneToMany relationship in there. And this
	 * method can contain a switch which queries corresponding list. Will have no
	 * affect to the outside part.
	 */
	public List<OrganizationBean> getOrganizations(OrganizationUserRole role) {
		List<OrganizationBean> items = new ArrayList<OrganizationBean>();
		for (OrganizationUserMapBean map : organizationUserMaps) {
			if (map.getRole().equals(role)) {
				items.add(map.getOrganization());
			}
		}
		return items;
	}
	
	public List<RepositoryBean> getRepositories(RepositoryUserRole role) {
		List<RepositoryBean> items = new ArrayList<RepositoryBean>();
		for (RepositoryUserMapBean map : repositoryUserMaps) {
			if (map.getRole().equals(role)) {
				items.add(map.getRepository());
			}
		}
		return items;
	}
	
	void addMap(OrganizationUserMapBean map) {
		organizationUserMaps.add(map);
	}
	
	boolean removeMap(OrganizationUserMapBean map) {
		return organizationUserMaps.remove(map);
	}
	
	void addMap(RepositoryUserMapBean map) {
		repositoryUserMaps.add(map);
	}
	
	boolean removeMap(RepositoryUserMapBean map) {
		return repositoryUserMaps.remove(map);
	}
	
	public boolean addSshKey(SshKeyBean sshKey) {
		return sshKeys.add(sshKey);
	}
}

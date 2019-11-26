package com.gitenter.protease.domain.auth;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.MapBean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "auth", name = "repository_user_map")
public class RepositoryUserMapBean implements MapBean<RepositoryBean,UserBean,RepositoryUserRole> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="repository_id")
	private RepositoryBean repository;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private UserBean user;
	
	/*
	 * Rather than a lookup table in SQL, we define the
	 * enum types in the persistent layer.
	 * 
	 * Refer to:
	 * https://stackoverflow.com/questions/2751733/map-enum-in-jpa-with-fixed-values
	 * https://www.thoughts-on-java.org/jpa-21-how-to-implement-type-converter/
	 * https://www.thoughts-on-java.org/jpa-21-type-converter-better-way-to/
	 * https://dzone.com/articles/mapping-enums-done-right
	 * 
	 * NOTE:
	 * Can't do inner classes of "RepositoryUserRole" and 
	 * "RepositoryUserRoleConventer". Failed to load ApplicationContext.
	 */
	@Setter(AccessLevel.NONE)
	@NotNull
	@Column(name="role_shortname")
	@Convert(converter = RepositoryUserRoleConventer.class)
	private RepositoryUserRole role;
	
	public void setRole(RepositoryUserRole role) {
		
		if (this.role != null && !this.role.equals(role)) {
			repository.getUserMaps(this.role).remove(this);
			repository.getUserMaps(role).add(this);
		}
		
		this.role = role;
	}
	
	public static RepositoryUserMapBean link(RepositoryBean repository, UserBean user, RepositoryUserRole role) {
		
		RepositoryUserMapBean map = new RepositoryUserMapBean();
		map.repository = repository;
		map.user = user;
		map.role = role;
		
		repository.addMap(map);
		user.addMap(map);
		
		return map;
	}
	
	@Override
	public boolean isAlterable(String operatorUsername) {
		
		String toBeDeletedUsername = user.getUsername();
		return !toBeDeletedUsername.equals(operatorUsername);
	}
}

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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "auth", name = "repository_person_map")
public class RepositoryPersonMapBean implements MapBean<RepositoryBean,PersonBean,RepositoryPersonRole> {

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
	@JoinColumn(name="person_id")
	private PersonBean person;
	
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
	 * Can't do inner classes of "RepositoryPersonRole" and 
	 * "RepositoryPersonRoleConventer". Failed to load ApplicationContext.
	 */
	@Column(name="role_shortname")
	@Convert(converter = RepositoryPersonRoleConventer.class)
	private RepositoryPersonRole role;
	
	public static RepositoryPersonMapBean link(RepositoryBean repository, PersonBean person, RepositoryPersonRole role) {
		
		RepositoryPersonMapBean map = new RepositoryPersonMapBean();
		map.repository = repository;
		map.person = person;
		map.role = role;
		
		repository.addMap(map);
		person.addMap(map);
		
		return map;
	}
	
	@Override
	public boolean isAlterable(String operatorUsername) {
		
		String toBeDeletedUsername = person.getUsername();
		return !toBeDeletedUsername.equals(operatorUsername);
	}
}

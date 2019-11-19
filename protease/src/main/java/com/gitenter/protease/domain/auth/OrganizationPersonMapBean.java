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
@Table(schema = "auth", name = "organization_person_map")
public class OrganizationPersonMapBean implements MapBean<OrganizationBean,PersonBean,OrganizationPersonRole> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="organization_id")
	private OrganizationBean organization;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="person_id")
	private PersonBean person;
	
	@NotNull
	@Column(name="role_shortname")
	@Convert(converter = OrganizationPersonRoleConventer.class)
	private OrganizationPersonRole role;

	/*
	 * TODO:
	 * 
	 * This is a general pattern method, but it seems quite hard to have a generic superclass
	 * "MapBean" implement this methods in there:
	 * 
	 * (1) Java cannot implement static method in generic types. It has the dirty workaround
	 * to make "this" link() rather than make this method as a factory.
	 * 
	 * (2) Then set attributes no longer work. A workaround is to define "setLeft()" and
	 * "setRight" and override in subclass. However, it gets messy as we actually should
	 * super the left/right attributes ("OrganizationBean" and "PersonBean" for this case),
	 * but it cannot be done as in this layer JPA need to use these attributes.
	 * 
	 * (3) We need some abstract superclass of "OrganizationBean" which implement "addMap()".
	 * 
	 * May be impossible but I can investigate later.
	 */
	public static OrganizationPersonMapBean link(OrganizationBean organization, PersonBean person, OrganizationPersonRole role) {
		
		OrganizationPersonMapBean map = new OrganizationPersonMapBean();
		map.organization = organization;
		map.person = person;
		map.role = role;
		
		organization.addMap(map);
		person.addMap(map);
		
		return map;
	}
	
	@Override
	public boolean isAlterable(String operatorUsername) {
		
		String toBeDeletedUsername = person.getUsername();
		return !toBeDeletedUsername.equals(operatorUsername);
	}
}

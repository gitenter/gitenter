package com.gitenter.protease.domain.auth;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.gitenter.protease.domain.MapBean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "auth", name = "organization_member_map")
public class OrganizationMemberMapBean implements MapBean<OrganizationBean,MemberBean,OrganizationMemberRole> {

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
	@JoinColumn(name="member_id")
	private MemberBean member;
	
	@NotNull
	@Column(name="role_shortname")
	@Convert(converter = OrganizationMemberRoleConventer.class)
	private OrganizationMemberRole role;

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
	 * super the left/right attributes ("OrganizationBean" and "MemberBean" for this case),
	 * but it cannot be done as in this layer JPA need to use these attributes.
	 * 
	 * (3) We need some abstract superclass of "OrganizationBean" which implement "addMap()".
	 * 
	 * May be impossible but I can investigate later.
	 */
	public static OrganizationMemberMapBean link(OrganizationBean organization, MemberBean member, OrganizationMemberRole role) {
		
		OrganizationMemberMapBean map = new OrganizationMemberMapBean();
		map.organization = organization;
		map.member = member;
		map.role = role;
		
		organization.addMap(map);
		member.addMap(map);
		
		return map;
	}
}

package com.gitenter.domain.settings;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "settings", name = "organization_member_map")
public class OrganizationMemberMapBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="organization_id")
	private OrganizationBean organization;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="member_id")
	private MemberBean member;
	
	@Column(name="role_shortname")
	@Convert(converter = OrganizationMemberRoleConventer.class)
	private OrganizationMemberRole role;
	
	public OrganizationMemberMapBean () {
		
	}

	public OrganizationMemberMapBean(OrganizationBean organization, MemberBean member, OrganizationMemberRole role) {
		this.organization = organization;
		this.member = member;
		this.role = role;
	}
}

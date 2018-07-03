package com.gitenter.domain.auth;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
public class OrganizationMemberMapBean {

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
	
	public OrganizationMemberMapBean () {
		
	}

	public OrganizationMemberMapBean(OrganizationBean organization, MemberBean member, OrganizationMemberRole role) {
		this.organization = organization;
		this.member = member;
		this.role = role;
	}
}

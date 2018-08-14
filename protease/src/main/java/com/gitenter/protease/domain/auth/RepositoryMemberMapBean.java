package com.gitenter.protease.domain.auth;

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
@Table(schema = "auth", name = "repository_member_map")
public class RepositoryMemberMapBean {

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
	@JoinColumn(name="member_id")
	private MemberBean member;
	
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
	 * Can't do inner classes of "RepositoryMemberRole" and 
	 * "RepositoryMemberRoleConventer". Failed to load ApplicationContext.
	 */
	@Column(name="role_shortname")
	@Convert(converter = RepositoryMemberRoleConventer.class)
	private RepositoryMemberRole role;
	
	public static RepositoryMemberMapBean link(RepositoryBean repository, MemberBean member, RepositoryMemberRole role) {
		
		RepositoryMemberMapBean map = new RepositoryMemberMapBean();
		map.repository = repository;
		map.member = member;
		map.role = role;
		
		repository.addMap(map);
		member.addMap(map);
		
		return map;
	}
}
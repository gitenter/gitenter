package com.gitenter.domain.settings;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
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
@Table(schema = "settings", name = "repository_member_map")
public class RepositoryMemberMapBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name="repository_id")
	private RepositoryBean repository;
	
	@ManyToOne
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
	
	public RepositoryMemberMapBean () {
		
	}

	public RepositoryMemberMapBean(RepositoryBean repository, MemberBean member, RepositoryMemberRole role) {
		this.repository = repository;
		this.member = member;
		this.role = role;
	}
}

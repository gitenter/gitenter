package com.gitenter.envelope.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.gitenter.protease.domain.auth.MemberBean;

import lombok.Getter;
import lombok.Setter;

/*
 * TODO:
 * 
 * Since all UI inputs are through these DTOs, we may consider
 * removing the validation part on all domain model beans.
 * As far as the database itself provides these constrains (checks)
 * there's not a lot of benefits to double put these in multiple
 * places.
 */
@Getter
@Setter
public class MemberProfileDTO {
	
	@NotNull
	@Size(min=2, max=16)
	private String username;
	
	@NotNull
	@Size(min=2, max=64)
	private String displayName;
	
	@NotNull
	@Email
	private String email;
	
	public void fillFromMemberBean (MemberBean memberBean) {
		
		/*
		 * Since password cannot be reversely analyzed,
		 * the corresponding item is just list as blank.
		 */
		this.username = memberBean.getUsername();
		this.displayName = memberBean.getDisplayName();
		this.email = memberBean.getEmail();
	}
	
	public void updateMemberBean (MemberBean memberBean) {
		
		/*
		 * Since this class doesn't cover all attributes of "MemberBean"
		 * (e.g., "password" is missing), it cannot create and return a
		 * "MemberBean" but can only modify one which already exist.
		 */
		assert (memberBean.getUsername().equals(username));
		
		memberBean.setDisplayName(displayName);
		memberBean.setEmail(email);
	}
}
package com.gitenter.capsid.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.capsid.service.exception.MaliciousOperationException;
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
public class MemberProfileDTO implements ReadDTO<MemberBean>, UpdateDTO<MemberBean> {
	
	@NotNull
	@Size(min=2, max=16)
	private String username;
	
	@NotNull
	@Size(min=2, max=64)
	private String displayName;
	
	@NotNull
	@Email
	private String email;
	
	@Override
	public void fillFromBean(MemberBean memberBean) {
		
		/*
		 * Since password cannot be reversely analyzed,
		 * the corresponding item is just list as blank.
		 */
		this.username = memberBean.getUsername();
		this.displayName = memberBean.getDisplayName();
		this.email = memberBean.getEmail();
	}
	
	@Override
	public void updateBean(MemberBean memberBean) {
		
		/*
		 * Since this class doesn't cover all attributes of "MemberBean"
		 * (e.g., "password" is missing), it cannot create and return a
		 * "MemberBean" but can only modify one which already exist.
		 */
		if (!memberBean.getUsername().equals(username)) {
			throw new MaliciousOperationException(
					"Somebody is trying to update user profile of somebody else.");
		}
		
		memberBean.setDisplayName(displayName);
		memberBean.setEmail(email);
	}
}

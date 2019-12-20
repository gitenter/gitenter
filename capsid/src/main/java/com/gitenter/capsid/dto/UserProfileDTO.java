package com.gitenter.capsid.dto;

import java.io.IOException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.capsid.service.exception.MaliciousOperationException;
import com.gitenter.protease.domain.auth.UserBean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@ToString
public class UserProfileDTO implements ReadDTO<UserBean>, UpdateDTO<UserBean> {
	
	@NotNull
	@Size(min=2, max=16)
	private String username;
	
	@NotNull
	@Size(min=2, max=64)
	private String displayName;
	
	@NotNull
	@NotBlank
	@Email
	private String email;
	
	@Override
	public void fillFromBean(UserBean userBean) {
		
		/*
		 * Since password cannot be reversely analyzed,
		 * the corresponding item is just list as blank.
		 */
		this.username = userBean.getUsername();
		this.displayName = userBean.getDisplayName();
		this.email = userBean.getEmail();
	}
	
	@Override
	public void updateBean(UserBean userBean) throws IOException {
		
		/*
		 * Since this class doesn't cover all attributes of "UserBean"
		 * (e.g., "password" is missing), it cannot create and return a
		 * "UserBean" but can only modify one which already exist.
		 */
		if (!userBean.getUsername().equals(username)) {
			throw new MaliciousOperationException("Somebody is trying to update user profile of somebody else.");
		}
		
		userBean.setDisplayName(displayName);
		userBean.setEmail(email);
	}
}

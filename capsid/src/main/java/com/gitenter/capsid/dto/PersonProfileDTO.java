package com.gitenter.capsid.dto;

import java.io.IOException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.capsid.service.exception.MaliciousOperationException;
import com.gitenter.protease.domain.auth.PersonBean;

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
public class PersonProfileDTO implements ReadDTO<PersonBean>, UpdateDTO<PersonBean> {
	
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
	public void fillFromBean(PersonBean personBean) {
		
		/*
		 * Since password cannot be reversely analyzed,
		 * the corresponding item is just list as blank.
		 */
		this.username = personBean.getUsername();
		this.displayName = personBean.getDisplayName();
		this.email = personBean.getEmail();
	}
	
	@Override
	public void updateBean(PersonBean personBean) throws IOException {
		
		/*
		 * Since this class doesn't cover all attributes of "PersonBean"
		 * (e.g., "password" is missing), it cannot create and return a
		 * "PersonBean" but can only modify one which already exist.
		 */
		if (!personBean.getUsername().equals(username)) {
			throw new MaliciousOperationException("Somebody is trying to update user profile of somebody else.");
		}
		
		personBean.setDisplayName(displayName);
		personBean.setEmail(email);
	}
}

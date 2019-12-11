package com.gitenter.capsid.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.gitenter.protease.domain.auth.UserBean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * TODO:
 * Cannot implements CreateReadUpdateDTO because the extra
 * argument `PasswordEncoder` for `toBean()`.
 */
@Getter
@Setter
@ToString(callSuper=true)
public class UserRegisterDTO extends UserProfileDTO {
	
	/*
	 * TODO:
	 * For safety concern, consider save the password in a character array
	 * rather than an array, and blank it once you are done with it.
	 * Reason: String is immutable and are stored in the Java String 
	 * pool, so it stays in there for an indeterminate period of
	 * time until being garbage collected.
	 */
	
	/*
	 * This password field cannot be @JsonIgnore because otherwise the 
	 * system will never try to parse it while handling a user input
	 * JSON with password.
	 */
	@ToString.Exclude
	@NotNull
	@Size(min=2, max=16)
	private String password;
	
	public UserBean toBean(PasswordEncoder passwordEncoder) {
		
		UserBean userBean = new UserBean();
		
		userBean.setUsername(getUsername());
		userBean.setPasswordHash(passwordEncoder.encode(password));
		userBean.setDisplayName(getDisplayName());
		userBean.setEmail(getEmail());
		userBean.setRegisterAt(new Date());
		
		return userBean;
	}
}

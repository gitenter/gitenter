package com.gitenter.capsid.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginDTO {

	/* 
	 * NOTE:
	 * Validation is actually not useful in here, as the POST request
	 * of login is not handled by the controller, but the Spring 
	 * Security package.
	 */
	@NotNull
	@Size(min=2, max=16)
	private String username;
	
	@NotNull
	@Size(min=2, max=16)
	@ToString.Exclude
	private String password;
}

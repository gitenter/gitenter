package com.gitenter.capsid.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {

	/*
	 * TODO:
	 * For safety concern, consider save the password in a character array
	 * rather than an array, and blank it once you are done with it.
	 * Reason: String is immutable and are stored in the Java String 
	 * pool, so it stays in there for an indeterminate period of
	 * time until being garbage collected.
	 */
	@NotNull
	@Size(min=2, max=16)
	private String oldPassword;
	
	@NotNull
	@Size(min=2, max=16)
	private String newPassword;
}

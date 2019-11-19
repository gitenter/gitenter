package com.gitenter.capsid.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.gitenter.protease.domain.auth.PersonBean;

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
public class PersonRegisterDTO extends PersonProfileDTO {
	
	/*
	 * TODO:
	 * For safety concern, consider save it in a character array
	 * rather than an array, and blank it once you are done with it.
	 * Reason: String is immutable and are stored in the Java String 
	 * pool, so it stays in there for an indeterminate period of
	 * time until being garbage collected.
	 */
	@NotNull
	@Size(min=2, max=16)
	@ToString.Exclude
	private String password;
	
	public PersonBean toBean(PasswordEncoder passwordEncoder) {
		
		PersonBean personBean = new PersonBean();
		
		personBean.setUsername(getUsername());
		personBean.setPassword(passwordEncoder.encode(password));
		personBean.setDisplayName(getDisplayName());
		personBean.setEmail(getEmail());
		personBean.setRegisterAt(new Date());
		
		return personBean;
	}
}

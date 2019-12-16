package com.gitenter.capsid.dto;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.gitenter.protease.domain.auth.SshKeyBean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * This class is used for validation only. Persistent is
 * irrelevant.
 * 
 * Spring doesn't fully support the validation beans to
 * be inner classes (Tiles/JSP raises errors for not getting
 * the bean value properly). Therefore, We takes them out
 * as independent outer classes.
 */
@Getter
@Setter
@ToString
public class SshKeyFieldDTO {

	/*
	 * See "AUTHORIZED_KEYS FILE FORMAT" section of sshd(8)
	 * https://www.freebsd.org/cgi/man.cgi?sshd(8)
	 * 
	 * Here allows "\n" on the right, as it will later be removed 
	 * while parsing.
	 */
	@NotNull
	@Pattern(
		regexp="^(ssh-rsa|ssh-dss|ecdsa-sha2-nistp256|ecdsa-sha2-nistp384|ecdsa-sha2-nistp521|ssh-ed25519) .*\\s*$",
		message="The SSH key does not have a valid format!")
	private String sshKeyValue;
	
	/*
	 * Cannot extend CreateDTO, as it raises special exceptions.
	 */
	public SshKeyBean toBean() throws GeneralSecurityException, IOException {
		
		SshKeyBean sshKeyBean = new SshKeyBean();
		sshKeyBean.setBean(sshKeyValue);
		
		return sshKeyBean;
	}
}
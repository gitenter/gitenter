package enterovirus.capsid.web.validation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SshKeyFieldBean {

	/*
	 * See "AUTHORIZED_KEYS FILE FORMAT" section of sshd(8)
	 * https://www.freebsd.org/cgi/man.cgi?sshd(8)#end
	 */
	@NotNull
	@Pattern(
		regexp="^(ssh-rsa|ssh-dss|ecdsa-sha2-nistp256|ecdsa-sha2-nistp384|ecdsa-sha2-nistp521|ssh-ed25519) .*$",
		message="The SSH key does not have a valid format.")
	private String value;
}

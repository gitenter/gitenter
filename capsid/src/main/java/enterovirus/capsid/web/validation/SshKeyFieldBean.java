package enterovirus.capsid.web.validation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

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
public class SshKeyFieldBean {

	/*
	 * See "AUTHORIZED_KEYS FILE FORMAT" section of sshd(8)
	 * https://www.freebsd.org/cgi/man.cgi?sshd(8)#end
	 * 
	 * Here allows "\n" on the right, as it will later be removed 
	 * while parsing.
	 */
	@NotNull
	@Pattern(
		regexp="^(ssh-rsa|ssh-dss|ecdsa-sha2-nistp256|ecdsa-sha2-nistp384|ecdsa-sha2-nistp521|ssh-ed25519) .*\\s*$",
		message="Wrong format")
	private String value;
}

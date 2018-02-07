package enterovirus.capsid.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {

	@NotNull
	@Size(min=2, max=16)
	private String username;
	
	@NotNull
	@Size(min=2, max=16)
	private String password;

	private String displayName;
	
	/*
	 * With @Email annotation, this column is not nullable.
	 */
	@Email
	private String email;

}

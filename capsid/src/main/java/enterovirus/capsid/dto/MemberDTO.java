package enterovirus.capsid.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import lombok.Getter;
import lombok.Setter;

/*
 * In general, the bean validation are in domain classes
 * (enterovirus.protease.domain package) rather than separated
 * DTO classes.
 * 
 * However, as we need to handle password encoding of the member
 * bean (in MemberService), we split the validation and the 
 * persistent parts of this bean.
 */
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

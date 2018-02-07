package enterovirus.capsid.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginDTO {

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
	private String password;
}

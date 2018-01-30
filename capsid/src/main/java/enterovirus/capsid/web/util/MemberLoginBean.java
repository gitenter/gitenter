package enterovirus.capsid.web.util;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/*
 * This class is used for validation only. Persistent is
 * irrelevant.
 */
@Getter
@Setter
public class MemberLoginBean {
	
	@NotNull
	@Size(min=2, max=16)
	private String username;
	
	@NotNull
	@Size(min=2, max=16)
	private String password;
}

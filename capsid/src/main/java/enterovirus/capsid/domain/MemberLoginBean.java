package enterovirus.capsid.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

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

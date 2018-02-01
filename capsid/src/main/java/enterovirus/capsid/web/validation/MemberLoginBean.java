package enterovirus.capsid.web.validation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class MemberLoginBean {
	
	@NotNull
	@Size(min=2, max=16)
	private String username;
	
	@NotNull
	@Size(min=2, max=16)
	private String password;
}

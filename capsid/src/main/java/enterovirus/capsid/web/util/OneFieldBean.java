package enterovirus.capsid.web.util;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OneFieldBean {

	@NotNull
	private String value;
}

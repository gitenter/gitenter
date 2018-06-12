package enterovirus.protease.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchBean {

	private String name;

	public BranchBean(String name) {
		super();
		this.name = name;
	}
}

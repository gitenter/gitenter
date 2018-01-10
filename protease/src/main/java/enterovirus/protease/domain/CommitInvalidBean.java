package enterovirus.protease.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_valid")
public class CommitInvalidBean extends CommitBean {

	@Column(name="error_message", updatable=false)
	private String errorMessage;
}

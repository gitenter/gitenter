package enterovirus.protease.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import enterovirus.gitar.wrap.CommitSha;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_valid")
public class CommitInvalidBean extends CommitBean {

	@Column(name="error_message", updatable=false)
	private String errorMessage;
	
	/*
	 * This default constructor is needed for Hibernate.
	 */
	public CommitInvalidBean () {
		super();
	}
	
	public CommitInvalidBean (RepositoryBean repository, CommitSha commitSha, String errorMessage) {
		super(repository, commitSha);
		this.errorMessage = errorMessage;
	}
}

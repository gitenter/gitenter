package com.gitenter.domain.git;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "invalid_commit")
public class InvalidCommitBean extends CommitBean {

	/*
	 * TODO:
	 * Can it show all the parsing exceptions at the same time?
	 * Or a better way is to have a client-side hook to handle that?
	 */
	@NotNull
	@Column(name="error_message", updatable=false)
	private String errorMessage;
}

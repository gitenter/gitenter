package com.gitenter.protease.domain.git;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.gitenter.gitar.GitCommit;
import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.auth.RepositoryBean;

import lombok.Getter;
import lombok.Setter;

/*
 * In SQL, use "Joined" inheritance Strategy out of four strategies
 * (1) Mapped Superclass
 * (2) Table per Class
 * (3) Single Table
 * (4) Joined
 */
@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit")
@Inheritance(strategy = InheritanceType.JOINED)
abstract public class CommitBean extends ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="repository_id")
	private RepositoryBean repository;
	
	@NotNull
	@Column(name="sha", updatable=false)
	private String sha;
	
	/*
	 * From git.
	 */
	@Transient
	private Date timestamp;
	
	@Transient
	private String message;
	
	@Transient
	private AuthorBean author;
	
	public void setFromGitCommit(GitCommit gitCommit) {
		
		sha = gitCommit.getSha();
		
		timestamp = gitCommit.getTimestamp();
		message = gitCommit.getMessage();
		author = AuthorBean.getInstance(gitCommit.getAuthor());
	}
	
	public static boolean inCommitList (String sha, List<CommitBean> commits) {
		
		for (CommitBean commit : commits) {
			if (commit.sha.equals(sha)) {
				return true;
			}
		}
		return false;
	}
}

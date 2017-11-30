package enterovirus.capsid.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit")
public class GitCommitBean {

	@Id
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="repository_id")
	private RepositoryBean repository;
	
	@Column(name="sha_checksum_hash", updatable=false)
	private String shaChecksumHash;
}

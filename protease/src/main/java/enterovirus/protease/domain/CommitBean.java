package enterovirus.protease.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import enterovirus.gitar.GitCommit;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit")
public class CommitBean {

	@Id
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="repository_id")
	private RepositoryBean repository;
	
	@Column(name="sha_checksum_hash", updatable=false)
	private String shaChecksumHash;
	
	@Transient
	private GitCommit.ListableTreeNode folderStructure;
	
	/*
	 * TODO:
	 * Get a more complicated Object inside of "TreeNode",
	 * and let it link to "DocumentBean" when needed.
	 * 
	 * Define a function which only show the part of the
	 * folder structure that include design document files.
	 */
	@OneToMany(targetEntity=DocumentBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="commit")
	private List<DocumentBean> documents;
}

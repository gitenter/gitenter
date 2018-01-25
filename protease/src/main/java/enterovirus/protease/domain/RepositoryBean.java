package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.*;

import enterovirus.gitar.wrap.*;

@Getter
@Setter
@Entity
@Table(schema = "config", name = "repository")
public class RepositoryBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="organization_id")
	private OrganizationBean organization;

	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@Column(name="display_name")
	private String displayName;

	@OneToMany(targetEntity=CommitBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	private List<CommitBean> commits = new ArrayList<CommitBean>();
	
	/*
	 * Lazy loaded by calling
	 * RepositoryGitDAO.loadBranchNames(repository)
	 * 
	 * TODO:
	 * Is there a way to lazily load them when call getter?
	 * If I don't want to wire RepositoryGitDAO to here to
	 * make this class no longer POJO... 
	 */
	@Transient
	private List<BranchName> branchNames;
	
	/*
	 * Lazy loaded by calling
	 * RepositoryGitDAO.loadCommitLog(repository, branch)
	 */
	@Transient
	private Map<CommitInfo,CommitBean> commitLogMap;
	
	public void addCommit (CommitBean commit) {
		commits.add(commit);
	}
}

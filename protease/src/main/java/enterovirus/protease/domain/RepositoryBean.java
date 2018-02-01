package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.Iterator;
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

	@OneToMany(targetEntity=RepositoryMemberMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	private List<RepositoryMemberMapBean> repositoryMemberMaps = new ArrayList<RepositoryMemberMapBean>();
	
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
	
	public void addMember (MemberBean member, RepositoryMemberRole role) {
		RepositoryMemberMapBean map = new RepositoryMemberMapBean(this, member, role);
		repositoryMemberMaps.add(map);
	}
	
	/*
	 * Not working, because repositoryRepository.saveAndFlush() cannot 
	 * really follow this change. Need to go with RepositoryMemberMapRepository.delete().
	 */
	public boolean removeMember (Integer memberId) {
		
		Iterator<RepositoryMemberMapBean> i = repositoryMemberMaps.iterator();
		while (i.hasNext()) {
			RepositoryMemberMapBean map = i.next();
			System.out.println(memberId+":"+map.getMember().getId());
			if (map.getMember().getId().equals(memberId)) {
				System.out.println("bingo");
				i.remove();
				return true;
			}
		}
		
		return false;
	}
	
	public RepositoryMemberMapBean getRepositoryMemberMap (Integer memberId) {
		for (RepositoryMemberMapBean map : repositoryMemberMaps) {
			if (map.getMember().getId().equals(memberId)) {
				return map;
			}
		}
		return null;
	}
}

package com.gitenter.protease.domain.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.protease.domain.ModelBean;
import com.gitenter.protease.domain.git.BranchBean;
import com.gitenter.protease.domain.git.CommitBean;
import com.gitenter.protease.domain.git.TagBean;
import com.gitenter.protease.domain.review.ReviewBean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "auth", name = "repository")
public class RepositoryBean extends ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="organization_id")
	private OrganizationBean organization;

	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@NotNull
	@Size(min=2, max=64)
	@Column(name="display_name")
	private String displayName;

	@Column(name="description")
	private String description;
	
	@NotNull
	@Column(name="is_public")
	private Boolean isPublic;
	
	@LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(targetEntity=CommitBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	/*
	 * Since to access through here, the commits are not persistent
	 * to git materials.
	 */
	@Getter(AccessLevel.NONE)
	private List<CommitBean> commits = new ArrayList<CommitBean>();
	
	/*
	 * Hibernate will smartly `select count(id) from git.git_commit`
	 */
	public int getCommitCount() {
		return commits.size();
	}

	@OneToMany(targetEntity=RepositoryMemberMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	private List<RepositoryMemberMapBean> repositoryMemberMaps = new ArrayList<RepositoryMemberMapBean>();
	
	@Transient
	@Getter(AccessLevel.NONE)
	private BranchPlaceholder branchPlaceholder;
	
	/*
	 * Return the desired "BranchBean" object based on the input name. No need to
	 * query the git storage to confirm the branch's existence.
	 */
	public BranchBean getBranch(String branchName) {
		return branchPlaceholder.get(branchName);
	}
	
	public interface BranchPlaceholder {
		BranchBean get(String branchName);
	}
	
	@Transient
	@Getter(AccessLevel.NONE)
	private BranchesPlaceholder branchesPlaceholder;
	
	public Collection<BranchBean> getBranches() throws IOException, GitAPIException {
		return branchesPlaceholder.get();
	}
	
	public List<String> getBranchNames() throws IOException, GitAPIException {
		List<String> branchNames = new ArrayList<String>();
		for (BranchBean branch : getBranches()) {
			branchNames.add(branch.getName());
		}
		return branchNames;
	}
	
	public interface BranchesPlaceholder extends GitPlaceholder<Collection<BranchBean>> {
		Collection<BranchBean> get() throws IOException, GitAPIException;
	}
	
	@Transient
	@Getter(AccessLevel.NONE)
	private TagPlaceholder tagPlaceholder;
	
	public TagBean getTag(String tagName) {
		return tagPlaceholder.get(tagName);
	}
	
	public interface TagPlaceholder {
		TagBean get(String tagName);
	}
	
	@Transient
	@Getter(AccessLevel.NONE)
	private TagsPlaceholder tagsPlaceholder;
	
	public Collection<TagBean> getTags() throws IOException, GitAPIException {
		return tagsPlaceholder.get();
	}
	
	public interface TagsPlaceholder extends GitPlaceholder<Collection<TagBean>> {
		Collection<TagBean> get() throws IOException, GitAPIException;
	}
	
	@OneToMany(targetEntity=ReviewBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	private List<ReviewBean> reviews;
	
	public Collection<MemberBean> getMembers(RepositoryMemberRole role) {
		Collection<MemberBean> items = new ArrayList<MemberBean>();
		for (RepositoryMemberMapBean map : repositoryMemberMaps) {
			if (map.getRole().equals(role)) {
				items.add(map.getMember());
			}
		}
		return items;
	}
	
	public void addCommit (CommitBean commit) {
		commits.add(commit);
	}
	
	void addMap(RepositoryMemberMapBean map) {
		repositoryMemberMaps.add(map);
	}
	
//	public void addMember (MemberBean member, RepositoryMemberRole role) {
//		RepositoryMemberMapBean map = new RepositoryMemberMapBean();
//		map.setRepository(this);
//		map.setMember(member);
//		map.setRole(role);
//		repositoryMemberMaps.add(map);
//	}
	
	/*
	 * Not working, because repositoryRepository.saveAndFlush() cannot 
	 * really follow this change. Need to go with RepositoryMemberMapRepository.delete().
	 */
//	public boolean removeMember (Integer memberId) {
//		
//		Iterator<RepositoryMemberMapBean> i = repositoryMemberMaps.iterator();
//		while (i.hasNext()) {
//			RepositoryMemberMapBean map = i.next();
//			System.out.println(memberId+":"+map.getMember().getId());
//			if (map.getMember().getId().equals(memberId)) {
//				System.out.println("bingo");
//				i.remove();
//				return true;
//			}
//		}
//		
//		return false;
//	}
//	
//	public RepositoryMemberMapBean getRepositoryMemberMap (Integer memberId) {
//		for (RepositoryMemberMapBean map : repositoryMemberMaps) {
//			if (map.getMember().getId().equals(memberId)) {
//				return map;
//			}
//		}
//		return null;
//	}
}

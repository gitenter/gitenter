package com.gitenter.domain.settings;

import java.util.ArrayList;
import java.util.Iterator;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gitenter.domain.git.CommitBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "settings", name = "repository")
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

	@NotNull
	@Size(min=2, max=64)
	@Column(name="display_name")
	private String displayName;

	@Column(name="description")
	private String description;
	
	@NotNull
	@Column(name="is_public")
	private Boolean isPublic;
	
	@OneToMany(targetEntity=CommitBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	private List<CommitBean> commits = new ArrayList<CommitBean>();

	@OneToMany(targetEntity=RepositoryMemberMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	private List<RepositoryMemberMapBean> repositoryMemberMaps = new ArrayList<RepositoryMemberMapBean>();
	
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
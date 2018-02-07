package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "config", name = "member")
public class MemberBean {

	/*
	 * @GeneratedValue for automatically generate primary keys.
	 * 
	 * PostgreSQL has some problem with Hibernate for automatic 
	 * primary key generation. Basically only strategy=GenerationType.IDENTITY 
	 * works, but it has performance issues (compare to SEQUENCE) -- 
	 * not crucial for us.
	 * 
	 * If our dummy data is made by INSERT using specific primary key, 
	 * then it doesn't change the PostgreSQL's SEQUENCE so if later 
	 * we insert without primary key (or let Hibernate to insert) 
	 * that will cause ID conflict issues. But if we ALTER SEQUENCE
	 * in postgres then everything will goes fine.
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@Column(name="username")
	private String username;
	
	@Column(name="password")
	private String password;

	@Column(name="display_name")
	private String displayName;
	
	@Column(name="email")
	private String email;
	
	@ManyToMany(targetEntity=OrganizationBean.class, mappedBy="managers", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<OrganizationBean> organizations;
	
	@OneToMany(targetEntity=RepositoryMemberMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="member")
	private List<RepositoryMemberMapBean> repositoryMemberMaps = new ArrayList<RepositoryMemberMapBean>();
	
	@OneToMany(targetEntity=SshKeyBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="member")
	private List<SshKeyBean> sshKeys = new ArrayList<SshKeyBean>();
}

package enterovirus.capsid.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "config", name = "organization")
public class OrganizationBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@Column(name="display_name")
	private String displayName;
	
	@JsonManagedReference
	@OneToMany(targetEntity=RepositoryBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="organization")
	private List<RepositoryBean> repositories;
	
	@ManyToMany(targetEntity=MemberBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(
			schema = "config", name="organization_manager_map",		
			joinColumns=@JoinColumn(name="organization_id", referencedColumnName="id"), 
			inverseJoinColumns=@JoinColumn(name="member_id", referencedColumnName="id"))
	@JsonBackReference
	private List<MemberBean> managers;
}

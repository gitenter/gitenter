package enterovirus.proteinsistence.domain;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

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
	@JsonBackReference
	private OrganizationBean organization;

	@NotNull
	@Size(min=2, max=16)
	@Column(name="name")
	private String name;

	@Column(name="display_name")
	private String displayName;

	@NotNull
	@Column(name="git_uri")
	private String gitUri;
	
	@OneToMany(targetEntity=CommitBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="repository")
	private List<CommitBean> commits;
}

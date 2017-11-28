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
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "config", name = "member")
public class MemberInfoBean {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@NotNull
	@Size(min=2, max=16)
	@Column(name="username")
	private String username;
	
	@NotNull
	@Size(min=2, max=16)
	@Column(name="password")
	@JsonIgnore
	private String password;

	@Column(name="display_name")
	private String displayName;
	
	@Email
	@Column(name="email")
	private String email;
	
	@ManyToMany(targetEntity=OrganizationBean.class, mappedBy="managers", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonManagedReference
	private List<OrganizationBean> organizations;
}

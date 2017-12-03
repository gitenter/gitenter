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

	@NotNull
	@Size(min=2, max=16)
	@Column(name="username")
	private String username;
	
	/*
	 * Here cannot go with @JsonIgnore since the password need to be saved.
	 */
	@NotNull
	@Size(min=2, max=16)
	@Column(name="password")
	private String password;

	@Column(name="display_name")
	private String displayName;
	
	/*
	 * Will @Email annotation, this column is not nullable.
	 */
	@Email
	@Column(name="email")
	private String email;
	
	@ManyToMany(targetEntity=OrganizationBean.class, mappedBy="managers", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JsonIgnore
	private List<OrganizationBean> organizations;
}

package enterovirus.capsid.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;

import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "config", name = "member")
public class NewMemberBean {

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
	
	@Email
	@Column(name="email")
	private String email;
}

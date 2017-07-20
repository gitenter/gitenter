package enterovirus.capsid.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.*;

@Entity
@Table(schema = "config", name = "organization")
@Getter
@Setter
public class OrganizationBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@Column(name="name")
	private String name;

	@Column(name="display_name")
	private String displayName;
}

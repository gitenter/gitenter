package enterovirus.capsid.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "current_document")
public class DocumentBean {

	@Id
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="commit_id")
	private GitCommitBean commit;
	
	@Column(name="filepath", updatable=false)
	private String filepath;
}
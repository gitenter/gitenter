package enterovirus.proteinsistence.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	private CommitBean commit;
	
	@Column(name="relative_filepath", updatable=false)
	private String relativeFilepath;
	
	/*
	 * @Transient is to specify that the property or field is not persistent.
	 */
	@Transient
	private List<LineContentBean> lineContents = new ArrayList<LineContentBean>();
	
	public void addLineContent(LineContentBean lineContent) {
		lineContents.add(lineContent);
	}
}
package enterovirus.protease.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/*
 * In SQL, use "Joined" inheritance Strategy out of four strategies
 * (1) Mapped Superclass
 * (2) Table per Class
 * (3) Single Table
 * (4) Joined
 */
@Getter
@Setter
@Entity
@Table(schema = "git", name = "document")
@Inheritance(strategy = InheritanceType.JOINED)
public class DocumentBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="commit_id")
	private CommitBean commit;
	
	public String getRelativeFilepath () {
		if (this instanceof DocumentModifiedBean) {
			return ((DocumentModifiedBean)this).getRelativeFilepath();
		}
		else {
			return (((DocumentUnmodifiedBean)this).getOriginalDocument()).getRelativeFilepath();
		}
	}
	
	public List<LineContentBean> getLineContents () {
		if (this instanceof DocumentModifiedBean) {
			return ((DocumentModifiedBean)this).getLineContents();
		}
		else {
			return (((DocumentUnmodifiedBean)this).getOriginalDocument()).getLineContents();
		}		
	}
}
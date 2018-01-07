package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	
	@OneToMany(targetEntity=TraceabilityMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="upstreamDocument")
	private List<TraceabilityMapBean> mapsForUpstreamItems = new ArrayList<TraceabilityMapBean>();

	@OneToMany(targetEntity=TraceabilityMapBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="downstreamDocument")
	private List<TraceabilityMapBean> mapsForDownstreamItems = new ArrayList<TraceabilityMapBean>();
	
	/*
	 * This default constructor is needed for Hibernate.
	 */
	public DocumentBean () {
		
	}
	
	public DocumentBean (CommitBean commit) {
		this.commit = commit;
	}
	
	public boolean addMapForAUpstreamItem (TraceabilityMapBean map) {
		return mapsForUpstreamItems.add(map);
	}
	
	public boolean addMapForADownstreamItem (TraceabilityMapBean map) {
		return mapsForDownstreamItems.add(map);
	}
	
	public DocumentModifiedBean getOriginalDocument () {
		if (this instanceof DocumentModifiedBean) {
			return (DocumentModifiedBean)this;
		}
		else {
			return ((DocumentUnmodifiedBean)this).getOriginalDocument();
		}
	}
	
	public String getRelativeFilepath () {
		return getOriginalDocument().getRelativeFilepath();
	}
	
	public String getContent () {
		return getOriginalDocument().getContent();		
	}
}
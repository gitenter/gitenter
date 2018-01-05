package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "document_modified")
public class DocumentModifiedBean extends DocumentBean {
	
	@Column(name="relative_filepath", updatable=false)
	private String relativeFilepath;
	
	@OneToMany(targetEntity=TraceableItemBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="originalDocument")
	private List<TraceableItemBean> traceableItems = new ArrayList<TraceableItemBean>();
	
	/*
	 * @Transient is to specify that the property or field is not persistent.
	 * 
	 * TODO:
	 * Since TraceableItemBean cannot extend LineContentBean (because of JPA
	 * annotation), should we completely remove this one, and replace it with
	 * "String blobContent"?
	 */
	@Transient
	private List<LineContentBean> lineContents = new ArrayList<LineContentBean>();
	
	/*
	 * This default constructor is needed for Hibernate.
	 */
	public DocumentModifiedBean () {
		
	}
	
	public DocumentModifiedBean (CommitBean commit, String relativeFilepath) {
		super(commit);
		this.relativeFilepath = relativeFilepath;
	}
	
	public boolean addTraceableItem(TraceableItemBean traceableItem) {
		return traceableItems.add(traceableItem);
	}
	
	public boolean addLineContent(LineContentBean lineContent) {
		return lineContents.add(lineContent);
	}
}
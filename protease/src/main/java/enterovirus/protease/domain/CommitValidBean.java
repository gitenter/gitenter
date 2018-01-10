package enterovirus.protease.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import enterovirus.gitar.GitFolderStructure;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_valid")
public class CommitValidBean extends CommitBean {

	@Transient
	private GitFolderStructure.ListableTreeNode folderStructure;
	
	/*
	 * TODO:
	 * Get a more complicated Object inside of "TreeNode",
	 * and let it link to "DocumentBean" when needed.
	 * 
	 * Define a function which only show the part of the
	 * folder structure that include design document files.
	 */
	@OneToMany(targetEntity=DocumentBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="commit")
	private List<DocumentBean> documents = new ArrayList<DocumentBean>();
	
	public boolean addDocument (DocumentBean document) {
		return documents.add(document);
	}
}

package com.gitenter.domain.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.jgit.api.errors.GitAPIException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_valid")
public class CommitValidBean extends CommitBean {
	
	@OneToMany(targetEntity=DocumentBean.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="commit")
	private List<DocumentBean> documents = new ArrayList<DocumentBean>();
	
	@Transient
	@Getter(AccessLevel.NONE)
	private RootPlaceholder rootPlaceholder;
	
	/*
	 * TODO:
	 * Define a function which only show the part of the
	 * folder structure that include design document files.
	 */
	public FolderBean getRoot() throws IOException, GitAPIException {
		return rootPlaceholder.get();
	}
	
//	/*
//	 * Used to map "folderStructure" leaves to actual "DocumentBean".
//	 * Initialized by initDocumentMap()
//	 * 
//	 * NOTE:
//	 * 
//	 * The other possibility is to have a "TreeNode(Object arg0)" which 
//	 * instead to have "String" (filepath) in it, but have a "DocumentBean"
//	 * in it. It is not done that way, because
//	 * 
//	 * (1) It is technically annoying (and maybe also have performance overhead)
//	 * to copy tree structure. I am too lazy to do it.
//	 * 
//	 * (2) Some leaves in "folderStructure" (e.g. the image files) doesn't
//	 * have the corresponding "DocumentBean". In the map we may simply set
//	 * the value to be null, but in the other approach we can only have a
//	 * complicated structure so it can still wrap filepath.
//	 */
//	@Transient
//	private Map<String,DocumentBean> documentMap;
	
//	/*
//	 * Lazily load by calling CommitGitDAO.loadFolderStructure(this).
//	 */
//	@Transient
//	private GitFolderStructure.ListableTreeNode folderStructure;
	
//	/*
//	 * This default constructor is needed for Hibernate.
//	 */
//	public CommitValidBean () {
//		super();
//	}
//	
//	public CommitValidBean (RepositoryBean repository, String commitSha) {
//		super(repository, commitSha);
//	}
	
//	/*
//	 * TODO:
//	 * Any way like @PostReceive? But this is not a spring Bean. 
//	 */
//	public void initDocumentMap() throws Exception {
//		documentMap = new HashMap<String,DocumentBean>();
//		for (DocumentBean document : documents) {
//			documentMap.put(document.getRelativeFilepath(), document);
//		}		
//	}
	
	public boolean addDocument (DocumentBean document) {
		return documents.add(document);
	}
	
	public interface RootPlaceholder {
		FolderBean get() throws IOException, GitAPIException;
	}
}

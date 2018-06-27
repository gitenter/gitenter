package com.gitenter.domain.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

import com.gitenter.gitar.util.GitPlaceholder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "git_commit_valid")
public class CommitValidBean extends CommitBean {
	
	/*
	 * TODO:
	 * One-to-many objects will not be git-refreshed, unless we setup the 
	 * placeholders in beans (rather than in impl's). However, that is
	 * contradict with the desire for beans to be POJO. Think about a way
	 * how to handle that.
	 * 
	 * Possibility:
	 * To have a GitDAO which only @autowired gitSource, and define 
	 * placeholders in there (maybe in version 2 make them as annotations
	 * just like the JPA ones). Import those placeholders in beans.
	 */
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
	
	public interface RootPlaceholder extends GitPlaceholder<FolderBean> {
		FolderBean get() throws IOException, GitAPIException;
	}
	
	@Transient
	@Getter(AccessLevel.NONE)
	private FilePlaceholder filePlaceholder;
	
	public FileBean getFile(String relativePath) throws IOException, GitAPIException {
		return filePlaceholder.get(relativePath);
	}
	
	public interface FilePlaceholder {
		FileBean get(String relativePath) throws IOException, GitAPIException;
	}
	
	/*
	 * TODO:
	 * Is it possible to setup "DocumentBean" (rather than "FileBean") when
	 * "getRoot()"?
	 */
//	@Transient
//	private Map<String,DocumentBean> documentMap;
	
	/*
	 * TODO:
	 * Make it lazy when getDocument().
	 */
//	public void initDocumentMap() throws Exception {
//		documentMap = new HashMap<String,DocumentBean>();
//		for (DocumentBean document : documents) {
//			documentMap.put(document.getRelativeFilepath(), document);
//		}		
//	}
	
	public boolean addDocument (DocumentBean document) {
		return documents.add(document);
	}
}

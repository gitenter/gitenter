package com.gitenter.protease.domain.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.protease.domain.ModelBean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "valid_commit")
@Inheritance(strategy = InheritanceType.JOINED)
public class ValidCommitBean extends CommitBean implements ModelBean {
	
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
	 * 
	 * TODO:
	 * 
	 * This is the un-(git-)bootstrapped one. Previously setup as 
	 * `@Getter(AccessLevel.NONE)` and it is removed by testing reasons.
	 * Probably still want to set it up, but use the explicit name
	 * such as `getDocumentsGitUnbootstrapped()`.
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
	
	@Transient
	private Map<String,DocumentBean> documentMap;
	
	private void lazilyInitializeDocumentMap() {
		if (documentMap != null) {
			return;
		}
		
		documentMap = new HashMap<String,DocumentBean>();
		for (DocumentBean document : documents) {
			documentMap.put(document.getRelativePath(), document);
		}
	}
	
	public boolean includeDocument(String relativePath) {
		
		lazilyInitializeDocumentMap();
		return documentMap.containsKey(relativePath);
	}
	
	/*
	 * TODO:
	 * Is it possible to setup "DocumentBean" (rather than "FileBean") when
	 * "getRoot()"? And/or say combine "getFile()" and "getDocument()".
	 * 
	 * TODO:
	 * Should it be in here at all? Or should we just use 
	 * "DocumentRepository.findByCommitIdAndRelativePath()"
	 * or "DocumentRepository.findByCommitShaAndRelativePath()"?
	 */
	public DocumentBean getDocument(String relativePath) throws IOException, GitAPIException {

		lazilyInitializeDocumentMap();
		DocumentBean item = documentMap.get(relativePath);
		
		if (item == null) {
			return null;
		}
		
		/*
		 * TODO:
		 * This is now working. At directly call "getDocments()" makes it not
		 * persistent to the git material. However, there's duplicated logic
		 * and code with other places.
		 * 
		 * Don't know if there's a better solution...
		 */
		FileBean file = this.getFile(relativePath);
		item.setName(file.name);
		item.setBlobContentPlaceholder(file.getBlobContentPlaceholder());
		item.setMimeTypePlaceholder(file.getMimeTypePlaceholder());
		
		return item;
	}
	
	public boolean addDocument(DocumentBean document) {
		boolean result = documents.add(document);
		return result;
	}
}

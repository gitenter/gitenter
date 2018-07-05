package com.gitenter.domain.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "valid_commit")
@Inheritance(strategy = InheritanceType.JOINED)
public class ValidCommitBean extends CommitBean {
	
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
	@Getter(AccessLevel.NONE)
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
	 * "getRoot()"? And/or say combine "getFile()" and "getDocument()".
	 * 
	 * TODO:
	 * Should it be in here at all? Or should we just use 
	 * "DocumentRepository.findByCommitIdAndRelativePath()"
	 * or "DocumentRepository.findByCommitShaAndRelativePath()"?
	 */
	public DocumentBean getDocument(String relativePath) throws IOException, GitAPIException {
		
		/*
		 * TODO:
		 * Setup a lazy evaluated index "Map<String,DocumentBean>" to handle that,
		 * rather than iterate the list every single time.
		 * 
		 * Naive common-lang LazyInitializer doesn't work, because:
		 * (1) It raises ConcurrentException, nor sure whether we need this complicity.
		 * (2) Its initialized is protected, so cannot refresh after "addDocument()".
		 */
		for (DocumentBean item : documents) {
			if (item.getRelativePath().equals(relativePath)) {
				
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
		}
		
		return null;
	}
	
	public boolean addDocument(DocumentBean document) {
		boolean result = documents.add(document);
		return result;
	}
}

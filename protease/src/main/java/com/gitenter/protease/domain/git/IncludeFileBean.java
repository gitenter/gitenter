package com.gitenter.protease.domain.git;

import java.io.IOException;

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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.ModelBean;

import lombok.Getter;
import lombok.Setter;

/*
 * It seems if we extend another bean, the attributes
 * in the superclass automatically becomes @Transient.
 */
@Getter
@Setter
@Entity
@Table(schema = "git", name = "include_file")
@Inheritance(strategy = InheritanceType.JOINED)
public class IncludeFileBean extends FileBean implements ModelBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="commit_id")
	private ValidCommitBean commit;
	
	@NotNull
	@Column(name="relative_path", updatable=false)
	private String relativePath;
	
	public String getContent() throws IOException, GitAPIException {
		return new String(getBlobContent());
	}
}
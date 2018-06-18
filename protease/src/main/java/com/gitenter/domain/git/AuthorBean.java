package com.gitenter.domain.git;

import com.gitenter.gitar.GitAuthor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorBean {

	private String name;
	private String emailAddress;
	
	public AuthorBean(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
	}
	
	public AuthorBean(GitAuthor author) {
		this.name = author.getName();
		this.emailAddress = author.getEmailAddress();
	}
}

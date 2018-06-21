package com.gitenter.domain.git;

import java.util.Hashtable;
import java.util.Map;

import com.gitenter.gitar.GitAuthor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorBean {
	
	private static Map<String,AuthorBean> instances = new Hashtable<String,AuthorBean>();

	private String name;
	private String emailAddress;
	
	private AuthorBean(String name, String emailAddress) {
		this.name = name;
		this.emailAddress = emailAddress;
	}
	
	static AuthorBean getInstance(String name, String emailAddress) {
	
		/*
		 * Use email address as the unique key, even if the author name
		 * is different (update author name in case it is needed).
		 */
		if (instances.containsKey(emailAddress)) {
			AuthorBean instance = instances.get(emailAddress);
			if (!instance.name.equals(name)) {
				instance.setName(name);
			}
			return instance;
		}
		else {
			AuthorBean instance = new AuthorBean(name, emailAddress);
			instances.put(emailAddress, instance);
			return instance;
		}
	}
	
	static AuthorBean getInstance(GitAuthor author) {
		return getInstance(author.getName(), author.getEmailAddress());
	}
}

package com.gitenter.protease.domain.git;

import com.gitenter.protease.domain.Role;

import lombok.Getter;

@Getter
public enum FileType implements Role {

	MARKDOWN("markdown"),
	GHERKIN("gherkin");
	
	private String name;
	
	private FileType(String name) {
		this.name = name;
	}
	
	public static FileType fromName(String name) {
		switch (name) {
		case "markdown":
			return MARKDOWN;
		case "gherkin":
			return GHERKIN;
		
		default:
			return null;
		}
	}
	
	public static FileType fromMimeType(String mimeType) {
		switch (mimeType) {
		case "text/markdown":
			return MARKDOWN;

		/*
		 * TODO:
		 * Add other mime types, include Gherkin.
		 */
		
		default:
			return null;
		}
	}
}

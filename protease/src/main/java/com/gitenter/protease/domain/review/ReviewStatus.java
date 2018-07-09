package com.gitenter.protease.domain.review;

import lombok.Getter;

@Getter
public enum ReviewStatus {

	APPROVED('A', "approved"),
	APPROVED_WITH_POSTSCRIPTS('P', "approved with postscript"),
	REQUEST_CHANGES('R', "request changes"),
	DENIED('D', "denied");
	
	private Character shortName;
	private String displayName;

	private ReviewStatus(Character shortName, String displayName) {
		this.shortName = shortName;
		this.displayName = displayName;
	}

	public static ReviewStatus fromShortName(Character shortName) {
		switch (shortName) {
		case 'A':
			return APPROVED;
		case 'P':
			return APPROVED_WITH_POSTSCRIPTS;
		case 'R':
			return REQUEST_CHANGES;
		case 'D':
			return DENIED;
		
		default:
			throw new IllegalArgumentException();
		}
	}
}

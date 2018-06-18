package com.gitenter.domain.git;

import lombok.*;

@Getter
@Setter
public class BlobBean {

	private byte[] blobContent;
	
	private String mimeType;
	
	public BlobBean(byte[] blobContent, String mimeType) {
		this.blobContent = blobContent;
		this.mimeType = mimeType;
	}
	
	public BlobBean () {
		
	}
}

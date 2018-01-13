package enterovirus.protease.domain;

import lombok.*;

@Getter
@Setter
public class BlobBean {
	
	private byte[] blobContent;
	
	private String mimeType;
}

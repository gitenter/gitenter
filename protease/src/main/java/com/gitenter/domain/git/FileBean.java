package com.gitenter.domain.git;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.util.GitPlaceholder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileBean extends PathBean {
	
	@Setter
	private BlobContentPlaceholder blobContentPlaceholder;
	
	public byte[] getBlobContent() throws IOException, GitAPIException {
		return blobContentPlaceholder.get();
	}
	
	public interface BlobContentPlaceholder extends GitPlaceholder<byte[]> {
		byte[] get() throws IOException, GitAPIException;
	}
	
	/*
	 * TODO:
	 * getMimeType()
	 */
}

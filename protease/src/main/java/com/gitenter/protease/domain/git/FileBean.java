package com.gitenter.protease.domain.git;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.GitHistoricalFile;
import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.gitar.util.GitProxyPlaceholder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileBean extends PathBean {
	
	private BlobContentPlaceholder blobContentPlaceholder;
	
	public byte[] getBlobContent() throws IOException, GitAPIException {
		return blobContentPlaceholder.get();
	}
	
	public interface BlobContentPlaceholder extends GitPlaceholder<byte[]> {
		byte[] get() throws IOException, GitAPIException;
	}
	
	private MimeTypePlaceholder mimeTypePlaceholder;
	
	public String getMimeType() throws IOException, GitAPIException {
		return mimeTypePlaceholder.get();
	}
	
	public interface MimeTypePlaceholder extends GitPlaceholder<String> {
		String get() throws IOException, GitAPIException;
	}
	
	/*
	 * TODO:
	 * 
	 * It is quite weird to have this method, and the following placeholder class
	 * definition in here. That makes this class less POJO.
	 * 
	 * However, there's no other place to move unless we are okay with duplicated
	 * code. Wonder whether there's a better solution.
	 */
	public void setFromGit(GitHistoricalFile gitFile) {
		
		super.setFromGit(gitFile);
		
		/*
		 * Technically, since "blobContentPlaceholder" and "mimeTypePlaceholder"
		 * refers to the same "gitFile", "gitFile.getBlobContent()" will only
		 * need to be queried once.
		 */
		blobContentPlaceholder = new ProxyBlobContentPlaceholder(gitFile);
		mimeTypePlaceholder = new ProxyMimeTypePlaceholder(gitFile);
	}
	
	private static class ProxyBlobContentPlaceholder extends GitProxyPlaceholder<byte[]> implements FileBean.BlobContentPlaceholder {

		final private GitHistoricalFile gitFile;
		
		private ProxyBlobContentPlaceholder(GitHistoricalFile gitFile) {
			this.gitFile = gitFile;
		}

		@Override
		protected byte[] getReal() throws IOException, GitAPIException {
			return gitFile.getBlobContent();
		}
	}
	
	private static class ProxyMimeTypePlaceholder extends GitProxyPlaceholder<String> implements FileBean.MimeTypePlaceholder {

		final private GitHistoricalFile gitFile;
		
		private ProxyMimeTypePlaceholder(GitHistoricalFile gitFile) {
			this.gitFile = gitFile;
		}

		@Override
		protected String getReal() throws IOException, GitAPIException {
			return gitFile.getMimeType();
		}
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public boolean isFile() {
		return true;
	}
}

package com.gitenter.dao.util;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface Placeholder<T> {
	public T get() throws IOException, GitAPIException;
}

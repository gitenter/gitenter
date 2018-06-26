package com.gitenter.dao.util;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

abstract public class ProxyPlaceholder<T> implements Placeholder<T> {

	private T proxyValue;
	
	@Override
	public T get() throws IOException, GitAPIException {
		
		if (proxyValue == null) {
			proxyValue = getReal();
		}
		return proxyValue;
	}
	
	abstract protected T getReal() throws IOException, GitAPIException;
}

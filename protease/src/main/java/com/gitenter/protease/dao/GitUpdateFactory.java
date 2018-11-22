package com.gitenter.protease.dao;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.protease.domain.ModelBean;

public interface GitUpdateFactory<ConcreteModelBean extends ModelBean> {
	
	public void update(ConcreteModelBean model) throws IOException, GitAPIException;
}
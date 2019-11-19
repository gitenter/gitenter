package com.gitenter.protease.dao.auth;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class RepositoryPersonMapSqlImpl implements RepositoryPersonMapSql {

	@PersistenceContext private EntityManager em;

	@Override
	public int throughSqlDeleteById(Integer mapId) {
	
		final String sql = "DELETE FROM RepositoryPersonMapBean WHERE id = :mapId";
		return em.createQuery(sql).setParameter("mapId", mapId).executeUpdate();
	}

}

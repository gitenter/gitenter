package com.gitenter.protease.dao.auth;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class OrganizationMemberMapSqlImpl implements OrganizationMemberMapSql {

	@PersistenceContext private EntityManager em;

	@Override
	public int throughSqlDeleteById(Integer mapId) {
	
		final String sql = "DELETE FROM OrganizationMemberMapBean WHERE id = :mapId";
		return em.createQuery(sql).setParameter("mapId", mapId).executeUpdate();
	}

}

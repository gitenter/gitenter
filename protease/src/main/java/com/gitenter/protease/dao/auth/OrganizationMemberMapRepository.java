package com.gitenter.protease.dao.auth;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.gitenter.protease.domain.auth.OrganizationMemberMapBean;

public interface OrganizationMemberMapRepository extends PagingAndSortingRepository<OrganizationMemberMapBean, Integer>, OrganizationMemberMapSql {

	/*
	 * There is a Spring Data method "deleteById" doing the same thing.
	 * However, the problem for that is Hibernate is too smart to not
	 * doing any actual SQL query. Since "OrganizationMemberMap" is a
	 * modified @ManyToMany relation linking table, with double-link
	 * on both side, it seems extremely hard to cheat Hibernate to let 
	 * him know that this SQL should be executed (I tried to remove the
	 * link on both "MemberBean" and "OrganizationBean" side, but it is
	 * just not working).
	 * 
	 * Therefore, the current method I use is to directly generate SQL
	 * query so for sure it will be executed.
	 */
	public int throughSqldeleteById(Integer mapId);

	public OrganizationMemberMapBean saveAndFlush(OrganizationMemberMapBean map);
}

package com.gitenter.capsid.service;

import java.io.IOException;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitenter.capsid.dto.OrganizationDTO;
import com.gitenter.capsid.service.exception.IdNotExistException;
import com.gitenter.capsid.service.exception.InvalidOperationException;
import com.gitenter.capsid.service.exception.UnreachableException;
import com.gitenter.protease.dao.auth.PersonRepository;
import com.gitenter.protease.dao.auth.OrganizationPersonMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.PersonBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationPersonMapBean;
import com.gitenter.protease.domain.auth.OrganizationPersonRole;

@Service
public class OrganizationManagerServiceImpl implements OrganizationManagerService {
	
	private static final Logger auditLogger = LoggerFactory.getLogger("audit");
	
	@Autowired PersonRepository personRepository;
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationPersonMapRepository organizationPersonMapRepository;
	
	/*
	 * TODO:
	 * Transaction setup in case map.saveAndFlush raises an exception.
	 * Notice that a simple `@Transactional` will cause the application unable
	 * to catch `OrganizationNameNotUniqueException` to redirect to the creation page. 
	 * > o.s.t.i.TransactionInterceptor : Application exception overridden by commit exception
	 */
	@Override
	@PreAuthorize("isAuthenticated()")
	public void createOrganization(PersonBean me, OrganizationDTO organizationDTO) throws IOException {
		OrganizationBean organization = organizationDTO.toBean();
		try {
			/*
			 * Need to save first. Otherwise when saving 
			 * "OrganizationPersonMapBean", non-null error will
			 * be raised for "organization_id" column.
			 */
			organizationRepository.saveAndFlush(organization);
		}
		catch(PersistenceException e) {
			ExceptionConsumingPipeline.consumePersistenceException(e, organization);
		}
		
		/*
		 * Cannot using "personRepository" or "organizationRepository"
		 * to save. It will double-insert the target row and cause primary
		 * key error.
		 */
		OrganizationPersonMapBean map = OrganizationPersonMapBean.link(organization, me, OrganizationPersonRole.MANAGER);
		organizationPersonMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organizationBean, T(com.gitenter.protease.domain.auth.OrganizationPersonRole).MANAGER)")
	public void updateOrganization(
			Authentication authentication, 
			OrganizationBean organizationBean, 
			OrganizationDTO organizationDTO) throws IOException {
		
		if (!organizationBean.getName().equals(organizationDTO.getName())) {
			throw new UnreachableException("POST Request is generated from unexpected source. "
					+ "OrganizationDTO should have organization name "+organizationBean.getName()
					+ ", but it is actually "+organizationDTO.getName());
		}
		
		organizationDTO.updateBean(organizationBean);
		organizationRepository.saveAndFlush(organizationBean);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth. OrganizationPersonRole).MANAGER)")
	public void addOrganizationMember(OrganizationBean organization, PersonBean person) {

		OrganizationPersonMapBean map = OrganizationPersonMapBean.link(organization, person, OrganizationPersonRole.MEMBER);
		organizationPersonMapRepository.saveAndFlush(map);
	}
	
	private OrganizationPersonMapBean getOrganizationPersonMapBean(Integer organizationPersonMapId) throws IOException {
		
		Optional<OrganizationPersonMapBean> maps = organizationPersonMapRepository.findById(organizationPersonMapId);
		
		if (maps.isPresent()) {
			return maps.get();
		}
		else {
			throw new IdNotExistException(OrganizationPersonMapBean.class, organizationPersonMapId);
		}
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationPersonRole).MANAGER)")
	public void removeOrganizationMember(OrganizationBean organization, Integer organizationPersonMapId) throws IOException {
		
		OrganizationPersonMapBean map = getOrganizationPersonMapBean(organizationPersonMapId);
		
		/*
		 * Doesn't for the SQL operation part, since if the `organizationPersonMapId` does not
		 * exist then `DELECT` simply does nothing. The problem is the `@PreAuthorize` is only
		 * for the operator has authorization for the current organization, but have no
		 * requirement if the `mapId` belongs to a completely different organization. That's
		 * the reason this checking is important.
		 */
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableException("Remove organization member input not consistency. "
					+ "organizationPersonMapId "+organizationPersonMapId+" doesn't belong to the "
					+ "target organization "+organization);
		}
		
		organizationPersonMapRepository.throughSqlDeleteById(organizationPersonMapId);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationPersonRole).MANAGER)")
	public void addOrganizationManager(OrganizationBean organization, Integer organizationMemberMapId) throws IOException {
		
		OrganizationPersonMapBean map = getOrganizationPersonMapBean(organizationMemberMapId);
		
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableException("Add organization member input not consistency. "
					+ "organizationMemberMapId "+organizationMemberMapId+" doesn't belong to the "
					+ "target organization "+organization);
		}
		
		if (map.getRole().equals(OrganizationPersonRole.MANAGER)) {
			throw new UnreachableException("User is already a manager of the target organization.");
		}
		
		map.setRole(OrganizationPersonRole.MANAGER);
		organizationPersonMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationPersonRole).MANAGER)")
	public void removeOrganizationManager(
			Authentication authentication,
			OrganizationBean organization, 
			Integer organizationPersonMapId) throws IOException {
		
		OrganizationPersonMapBean map = getOrganizationPersonMapBean(organizationPersonMapId);
		
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableException("Remove organization member input not consistency. "
					+ "organizationPersonMapId "+organizationPersonMapId+" doesn't belong to the"
					+ " target organization "+organization);
		}
		
		if (!map.getRole().equals(OrganizationPersonRole.MANAGER)) {
			throw new UnreachableException("User is currently not a manager of the target organization. Current role "+map.getRole());
		}
		
		if (authentication.getName().equals(map.getPerson().getUsername())) {
			throw new InvalidOperationException("Rejected "+authentication.getName()+" to remove him/herself as a manager of organization "+organization);
		}
		
		map.setRole(OrganizationPersonRole.MEMBER);
		organizationPersonMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationPersonRole).MANAGER)")
	public void deleteOrganization(OrganizationBean organization) throws IOException {
		
		auditLogger.info("Organization has been deleted: "+organization);
		organizationRepository.delete(organization);
	}
}

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
import com.gitenter.protease.dao.auth.UserRepository;
import com.gitenter.protease.dao.auth.OrganizationUserMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.domain.auth.UserBean;
import com.gitenter.protease.domain.auth.OrganizationBean;
import com.gitenter.protease.domain.auth.OrganizationUserMapBean;
import com.gitenter.protease.domain.auth.OrganizationUserRole;

@Service
public class OrganizationManagerServiceImpl implements OrganizationManagerService {
	
	private static final Logger auditLogger = LoggerFactory.getLogger("audit");
	
	@Autowired UserRepository userRepository;
	@Autowired OrganizationRepository organizationRepository;
	@Autowired OrganizationUserMapRepository organizationUserMapRepository;
	
	/*
	 * TODO:
	 * Transaction setup in case map.saveAndFlush raises an exception.
	 * Notice that a simple `@Transactional` will cause the application unable
	 * to catch `OrganizationNameNotUniqueException` to redirect to the creation page. 
	 * > o.s.t.i.TransactionInterceptor : Application exception overridden by commit exception
	 */
	@Override
	@PreAuthorize("isAuthenticated()")
	public void createOrganization(UserBean me, OrganizationDTO organizationDTO) throws IOException {
		OrganizationBean organization = organizationDTO.toBean();
		try {
			/*
			 * Need to save first. Otherwise when saving 
			 * "OrganizationUserMapBean", non-null error will
			 * be raised for "organization_id" column.
			 */
			organizationRepository.saveAndFlush(organization);
		}
		catch(PersistenceException e) {
			ExceptionConsumingPipeline.consumePersistenceException(e, organization);
		}
		
		/*
		 * Cannot using "userRepository" or "organizationRepository"
		 * to save. It will double-insert the target row and cause primary
		 * key error.
		 */
		OrganizationUserMapBean map = OrganizationUserMapBean.link(organization, me, OrganizationUserRole.MANAGER);
		organizationUserMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organizationBean, T(com.gitenter.protease.domain.auth.OrganizationUserRole).MANAGER)")
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
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth. OrganizationUserRole).MANAGER)")
	public void addOrganizationMember(OrganizationBean organization, UserBean user) {

		OrganizationUserMapBean map = OrganizationUserMapBean.link(organization, user, OrganizationUserRole.MEMBER);
		organizationUserMapRepository.saveAndFlush(map);
	}
	
	private OrganizationUserMapBean getOrganizationUserMapBean(Integer organizationUserMapId) throws IOException {
		
		Optional<OrganizationUserMapBean> maps = organizationUserMapRepository.findById(organizationUserMapId);
		
		if (maps.isPresent()) {
			return maps.get();
		}
		else {
			throw new IdNotExistException(OrganizationUserMapBean.class, organizationUserMapId);
		}
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationUserRole).MANAGER)")
	public void removeOrganizationMember(OrganizationBean organization, Integer organizationUserMapId) throws IOException {
		
		OrganizationUserMapBean map = getOrganizationUserMapBean(organizationUserMapId);
		
		/*
		 * Doesn't for the SQL operation part, since if the `organizationUserMapId` does not
		 * exist then `DELECT` simply does nothing. The problem is the `@PreAuthorize` is only
		 * for the operator has authorization for the current organization, but have no
		 * requirement if the `mapId` belongs to a completely different organization. That's
		 * the reason this checking is important.
		 */
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableException("Remove organization user input not consistency. "
					+ "organizationUserMapId "+organizationUserMapId+" doesn't belong to the "
					+ "target organization "+organization);
		}
		
		organizationUserMapRepository.throughSqlDeleteById(organizationUserMapId);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationUserRole).MANAGER)")
	public void addOrganizationManager(OrganizationBean organization, Integer organizationUserMapId) throws IOException {
		
		OrganizationUserMapBean map = getOrganizationUserMapBean(organizationUserMapId);
		
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableException("Add organization user input not consistency. "
					+ "organizationUserMapId "+organizationUserMapId+" doesn't belong to the "
					+ "target organization "+organization);
		}
		
		if (map.getRole().equals(OrganizationUserRole.MANAGER)) {
			throw new UnreachableException("User is already a manager of the target organization.");
		}
		
		map.setRole(OrganizationUserRole.MANAGER);
		organizationUserMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationUserRole).MANAGER)")
	public void removeOrganizationManager(
			Authentication authentication,
			OrganizationBean organization, 
			Integer organizationUserMapId) throws IOException {
		
		OrganizationUserMapBean map = getOrganizationUserMapBean(organizationUserMapId);
		
		if (!map.getOrganization().getId().equals(organization.getId())) {
			throw new UnreachableException("Remove organization user input not consistency. "
					+ "organizationUserMapId "+organizationUserMapId+" doesn't belong to the"
					+ " target organization "+organization);
		}
		
		if (!map.getRole().equals(OrganizationUserRole.MANAGER)) {
			throw new UnreachableException("User is currently not a manager of the target organization. Current role "+map.getRole());
		}
		
		if (authentication.getName().equals(map.getUser().getUsername())) {
			throw new InvalidOperationException("Rejected "+authentication.getName()+" to remove him/herself as a manager of organization "+organization);
		}
		
		map.setRole(OrganizationUserRole.MEMBER);
		organizationUserMapRepository.saveAndFlush(map);
	}
	
	@Override
	@PreAuthorize("hasPermission(#organization, T(com.gitenter.protease.domain.auth.OrganizationUserRole).MANAGER)")
	public void deleteOrganization(OrganizationBean organization) throws IOException {
		
		auditLogger.info("Organization has been deleted: "+organization);
		organizationRepository.delete(organization);
	}
}

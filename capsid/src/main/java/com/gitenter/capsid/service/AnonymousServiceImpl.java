package com.gitenter.capsid.service;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitenter.capsid.dto.PersonRegisterDTO;
import com.gitenter.protease.dao.auth.PersonRepository;
import com.gitenter.protease.domain.auth.PersonBean;

@Service
public class AnonymousServiceImpl implements AnonymousService {

	@Autowired private PersonRepository personRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	
	/*
	 * TODO:
	 * Transaction setup. Notice that a simple `@Transactional` will cause the application unable
	 * to catch `UsernameNotUniqueException` to redirect to the registration page. Optional in here
	 * since even if we failed to send email the user should still be treated as registered.
	 * > o.s.t.i.TransactionInterceptor : Application exception overridden by commit exception
	 */
	@Override
	public void signUp(PersonRegisterDTO personRegisterDTO) throws IOException {
		
		PersonBean personBean = personRegisterDTO.toBean(passwordEncoder);
		try {
			personRepository.saveAndFlush(personBean);
		}
		catch(PersistenceException e) {
			ExceptionConsumingPipeline.consumePersistenceException(e, personBean);
		}
		
		/*
		 * TODO:
		 * Send confirmation email, ...
		 */
	}
}

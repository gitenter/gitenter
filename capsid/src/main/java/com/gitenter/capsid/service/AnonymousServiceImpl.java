package com.gitenter.capsid.service;

import java.io.IOException;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gitenter.capsid.dto.UserRegisterDTO;
import com.gitenter.protease.dao.auth.UserRepository;
import com.gitenter.protease.domain.auth.UserBean;

@Service
public class AnonymousServiceImpl implements AnonymousService {

	@Autowired private UserRepository userRepository;
	@Autowired private PasswordEncoder passwordEncoder;
	
	/*
	 * TODO:
	 * Transaction setup. Notice that a simple `@Transactional` will cause the application unable
	 * to catch `UsernameNotUniqueException` to redirect to the registration page. Optional in here
	 * since even if we failed to send email the user should still be treated as registered.
	 * > o.s.t.i.TransactionInterceptor : Application exception overridden by commit exception
	 */
	@Override
	public void signUp(UserRegisterDTO userRegisterDTO) throws IOException {
		
		UserBean userBean = userRegisterDTO.toBean(passwordEncoder);
		try {
			userRepository.saveAndFlush(userBean);
		}
		catch(PersistenceException e) {
			ExceptionConsumingPipeline.consumePersistenceException(e, userBean);
		}
		
		/*
		 * TODO:
		 * Send confirmation email, ...
		 */
	}
}

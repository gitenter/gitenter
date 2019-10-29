package com.gitenter.capsid.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gitenter.capsid.dto.MemberProfileDTO;
import com.gitenter.capsid.dto.MemberRegisterDTO;
import com.gitenter.capsid.service.exception.UserNotExistException;
import com.gitenter.protease.dao.auth.MemberRepository;
import com.gitenter.protease.dao.auth.OrganizationMemberMapRepository;
import com.gitenter.protease.dao.auth.OrganizationRepository;
import com.gitenter.protease.dao.auth.SshKeyRepository;
import com.gitenter.protease.domain.auth.MemberBean;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class MemberServiceTest {

	@MockBean private MemberRepository memberRepository;
	@MockBean private OrganizationRepository organizationRepository;
	@MockBean private OrganizationMemberMapRepository organizationMemberMapRepository;
	@MockBean private SshKeyRepository sshKeyRepository;
	@MockBean private PasswordEncoder passwordEncoder;
	
	@Autowired private MemberService memberService;
	
	private MemberBean member;
	
	@BeforeEach
	public void setUp() throws Exception {

		given(passwordEncoder.encode("password")).willReturn("wordpass");
		given(passwordEncoder.matches("password", "wordpass")).willReturn(true);
		
		member = new MemberBean();
		member.setUsername("username");
		member.setPassword(passwordEncoder.encode("password"));
		member.setDisplayName("User Name");
		member.setEmail("username@email.com");
		List<MemberBean> members = new ArrayList<MemberBean>();
		members.add(member);
		
		given(memberRepository.findByUsername("username")).willReturn(members);
	}
	
	@Test
	public void testGetMemberByUsernameWithValidUsername() throws IOException {
		assertEquals(memberService.getMemberByUsername("username"), member);
	}
	
	@Test
	public void testGetMemberByUsernameWithInValidUsername() throws IOException {
		Assertions.assertThrows(UserNotExistException.class, () -> {
			memberService.getMemberByUsername("not_exist");
		});
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdateMemberWithAuthorizedUser() throws IOException {
		
		MemberProfileDTO profile = new MemberProfileDTO();
		profile.setUsername("username");
		profile.setDisplayName("Updated User Name");
		profile.setEmail("updated_username@email.com");
		
		memberService.updateMember(profile);
		
		/*
		 * `member` is updated, but that's the same pointer.
		 */
		verify(memberRepository, times(1)).saveAndFlush(member);
		assertEquals(member.getDisplayName(), "Updated User Name");
		assertEquals(member.getEmail(), "updated_username@email.com");
	}
	
	@Test
	@WithMockUser(username="hijacked_username")
	public void testUpdateMemberWithHijackedUser() throws IOException {
		
		MemberProfileDTO profile = new MemberProfileDTO();
		profile.setUsername("username");
		profile.setDisplayName("Updated User Name");
		profile.setEmail("updated_username@email.com");
		
		Assertions.assertThrows(AccessDeniedException.class, () -> {
			memberService.updateMember(profile);
		});
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdatePasswordCorrectOldPassword() throws IOException {
		
		MemberRegisterDTO register = new MemberRegisterDTO();
		register.setUsername("username");
		register.setPassword("new_password");
		
		assertTrue(memberService.updatePassword(register, "password"));
		
		verify(memberRepository, times(1)).saveAndFlush(member);
	}
	
	@Test
	@WithMockUser(username="username")
	public void testUpdatePasswordWrongOldPassword() throws IOException {
		
		MemberRegisterDTO register = new MemberRegisterDTO();
		register.setUsername("username");
		register.setPassword("new_password");
		
		assertFalse(memberService.updatePassword(register, "wrong_password"));
		verify(memberRepository, times(0)).saveAndFlush(member);
	}
}

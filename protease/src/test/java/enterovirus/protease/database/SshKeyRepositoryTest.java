package enterovirus.protease.database;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "user_auth")
@ContextConfiguration(classes=ProteaseConfig.class)
public class SshKeyRepositoryTest {

	@Autowired MemberRepository memberRepository;
	@Autowired SshKeyRepository sshKeyRepository;
	
	/*
	 * Ignored, because otherwise the database complains error:
	 * > ERROR: duplicate key value violates unique constraint "ssh_key_key_key"
	 */
	@Ignore
	public void test() throws IOException {
		
		MemberBean member = memberRepository.findById(1).get();
		
		SshKeyBean sshKey = new SshKeyBean();
		sshKey.setMember(member);
		sshKey.setKey("a-very-long-secret-key");
		
		sshKeyRepository.saveAndFlush(sshKey);
	}
	
	@Test
	public void testDummy () {
		
	}
}

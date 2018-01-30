package enterovirus.protease.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	@Test
	public void test() throws Exception {
		
		MemberBean member = memberRepository.findById(1);
		
		Path publicKeyFile = Paths.get("/home/beta/.ssh/id_rsa.pub");
		
		/*
		 * Read the first line, as it has only one line.
		 */
		String publicKeyLine = Files.lines(publicKeyFile).collect(Collectors.toList()).get(0);
		
		SshKeyBean sshKey = new SshKeyBean(publicKeyLine);
		sshKey.setMember(member);
		
		sshKeyRepository.saveAndFlush(sshKey);
	}
	
	@Test
	public void testDummy () {
		
	}
}

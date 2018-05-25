package enterovirus.protease.database;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

import enterovirus.protease.*;
import enterovirus.protease.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "production")
@ContextConfiguration(classes=ProteaseConfig.class)
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionalTestExecutionListener.class,
	DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection={"schemaSettingsDatabaseConnection"})
public class RepositoryMemberMapRepositoryTest {

	@Autowired RepositoryMemberMapRepository repository;
	
	@Test
	@DatabaseSetup(connection="schemaSettingsDatabaseConnection", value="dbunit-data/minimal-schema-settings.xml")
	public void test() throws IOException {
		List<RepositoryMemberMapBean> item = repository.findByUsernameAndRepositoryId("user", 1);
		assertEquals(item.size(), 1);
		assertEquals(item.get(0).getRole(), RepositoryMemberRole.REVIEWER);
	}
}

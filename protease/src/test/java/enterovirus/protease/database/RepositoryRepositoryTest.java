package enterovirus.protease.database;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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
import org.springframework.transaction.annotation.Transactional;

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
public class RepositoryRepositoryTest {

	@Autowired RepositoryRepository repository;
	
	@Test
	@Transactional
	@DatabaseSetup(connection="schemaSettingsDatabaseConnection", value="dbunit-data/minimal-schema-settings.xml")
	public void test() throws IOException {
		RepositoryBean item = repository.findById(1);
		assertEquals(item.getName(), "repository");
	}
}

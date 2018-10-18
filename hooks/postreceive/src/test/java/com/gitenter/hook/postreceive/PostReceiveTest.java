package com.gitenter.hook.postreceive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gitenter.hook.postreceive.PostReceiveConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "minimal")
@ContextConfiguration(classes=PostReceiveConfig.class)
public class PostReceiveTest {

	@Test
	public void testDbUnitMinimalQueryWorks() {
		
		assertEquals(true, false);
	}
}

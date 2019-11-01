package com.gitenter.capsid;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gitenter.capsid.web.HealthCheckController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("local")
public class SmokeTest {
	
	@Autowired private HealthCheckController controller;

	@Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}

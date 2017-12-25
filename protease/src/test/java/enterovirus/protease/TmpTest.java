package enterovirus.protease;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import enterovirus.protease.Tmp;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ComponentScanConfig.class)
public class TmpTest {

	@Autowired Tmp tmp;
	
	@Test
	public void test() throws IOException {
		System.out.println("hello world");
		System.out.println(tmp.find());
	}

}


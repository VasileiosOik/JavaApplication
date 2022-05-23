package com.connection.application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class ApplicationTest {

	@Test
	public void contextLoads() {
	}

}

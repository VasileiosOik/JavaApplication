package com.connection.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;

@Configuration
public class MongoDBConfiguration {

	private final Logger log = LoggerFactory.getLogger(MongoDBConfiguration.class);

	@Value("${spring.data.mongodb.host}")
	private String mongoUrl;

	@Value("${spring.data.mongodb.database}")
	private String mongoDBName;

	@Value("${spring.data.mongodb.port}")
	private int mongoDBPort;

	@Bean
	public MongoDbFactory mongoDbFactory() {
		log.debug("Creating the Mongo Connection");
		return new SimpleMongoDbFactory(new MongoClient(mongoUrl, mongoDBPort), mongoDBName);
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		log.debug("Creating the Mongo Template");
		return new MongoTemplate(mongoDbFactory());
	}

}

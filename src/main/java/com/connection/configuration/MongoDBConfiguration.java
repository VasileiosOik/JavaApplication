package com.connection.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

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
    public MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/Company");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        logConfiguration();
        return new MongoTemplate(mongo(), "Company");
    }

    private void logConfiguration() {
        log.info("========================= MongoDB Connection =====================");
        log.info("====== MongoDB url: {}", mongoUrl);
        log.info("====== MongoDB Port: {}", mongoDBPort);
        log.info("====== Collection Name: {}", mongoDBName);
        log.info("===================================================================");
    }
}

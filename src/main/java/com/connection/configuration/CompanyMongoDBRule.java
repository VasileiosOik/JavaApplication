package com.connection.configuration;

import com.lordofthejars.nosqlunit.mongodb.MongoDbConfiguration;
import com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

public class CompanyMongoDBRule extends MongoDbRule {

    public CompanyMongoDBRule() {
        super(MongoDbConfigurationBuilder.mongoDb().databaseName("Company").port(27017).host("localhost").build());
    }

    public CompanyMongoDBRule(MongoDbConfiguration mongoDbConfiguration) {
        super(mongoDbConfiguration);
    }
}

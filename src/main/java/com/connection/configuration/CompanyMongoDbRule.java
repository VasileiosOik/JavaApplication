package com.connection.configuration;

import com.lordofthejars.nosqlunit.mongodb.MongoDbConfiguration;
import com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

public class CompanyMongoDbRule extends MongoDbRule {

    public CompanyMongoDbRule() {
        super(MongoDbConfigurationBuilder.mongoDb().databaseName("Company").port(27017).host("localhost").build());
    }

    public CompanyMongoDbRule(MongoDbConfiguration mongoDbConfiguration) {
        super(mongoDbConfiguration);
    }
}

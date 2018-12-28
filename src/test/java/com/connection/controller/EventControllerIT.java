package com.connection.controller;

import com.connection.application.Application;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class EventControllerIT {

    private static final String APPLICATION_JSON = "application/json";

    @LocalServerPort
    private int port;

    @Before
    public void setUp() {

        RestAssured.port = port;
    }

    @Rule
    public MongoDbRule remoteMongoDbRule = new MongoDbRule(MongoDbConfigurationBuilder.mongoDb().databaseName("Company").port(27017).host("localhost").build());

    @Test
    @UsingDataSet(locations = "/mongoTestData/unitsJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void returnDateBetweenDates(){

        given()
                .contentType(APPLICATION_JSON)
        .when()
                .get("event/getEvents" + "?fromDate=" + "2018-07-05" + "&toDate=" + "2018-12-10" )
        .then()
                .log().everything()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].employeeId", equalTo(100016))
                .body("[1].employeeId", equalTo(100015));

    }
}

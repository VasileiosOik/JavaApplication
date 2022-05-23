package com.connection.controller;

import com.connection.application.Application;
import com.mongodb.BasicDBObject;
import io.restassured.RestAssured;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class EventControllerIT {

    private static final String APPLICATION_JSON = "application/json";

    @Autowired
    private MongoTemplate mongoTemplate;


    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @After
    public void tearUp() {
        mongoTemplate.remove(new Query(), "Company");
    }

    @Test
    public void returnDateBetweenDates() {

        //given
        BasicDBObject firstEmployee = new BasicDBObject();
        firstEmployee.append("employeeId", 100015).append("firstName", "Alex")
                .append("lastName", "dean").append("jobTitle", "Tester")
                .append("hireDate", Date.from(LocalDate.of(2016, 5, 29).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("managerId", 100004)
                .append("departmentId", 1003)
                .append("timeCreated", Date.from(LocalDate.of(2018, 9, 2).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        BasicDBObject secondEmployee = new BasicDBObject();
        secondEmployee.append("employeeId", 100016).append("firstName", "Bill")
                .append("lastName", "Eco").append("jobTitle", "Dev")
                .append("hireDate", Date.from(LocalDate.of(2016, 5, 29).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("managerId", 100004)
                .append("departmentId", 1003)
                .append("timeCreated", Date.from(LocalDate.of(2018, 11, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        mongoTemplate.save(new Document(firstEmployee), "Company");
        mongoTemplate.save(new Document(secondEmployee), "Company");


        // when
        given()
                .contentType(APPLICATION_JSON)
                .when()
                .get("event/getEvents" + "?fromDate=" + "2018-07-05" + "&toDate=" + "2018-12-10")
                .then()
                .log().everything()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].employeeId", equalTo(100016))
                .body("[1].employeeId", equalTo(100015));
    }
}

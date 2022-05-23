package com.connection.controller;

import com.connection.application.Application;
import com.connection.domain.Employee;
import com.connection.mapper.DepartmentBuilder;
import com.connection.mapper.EmployeeBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import io.restassured.RestAssured;
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
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class CompanyControllerIT {

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
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void getAllEmployees() {

		given()
                .contentType(APPLICATION_JSON)
        .when()
                .get("/company/employees")
        .then()
                .log().everything()
                .statusCode(200)
                .body("[0].id", equalTo(100001))
                .body("[0].name", equalTo("David"))
                .body("[1].id", equalTo(100002))
                .body("[1].name", equalTo("Kevin"));
	}

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testGetAnEmployees() {

        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", 100006)
                .when()
                .get("/company/oneemployee/{id}")
                .then()
                .log().everything()
                .statusCode(200)
                .body("id", equalTo(100006))
                .body("name", equalTo("Petra"));
    }

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testGetAllDepartments() {

		 given()
                 .contentType(APPLICATION_JSON)
         .when()
                 .get("/company/departments")
         .then()
                 .log().everything()
                 .statusCode(200)
                 .body("[0].depId", equalTo(1001))
                 .body("[0].depName", equalTo("Research"))
                 .body("[1].depId", equalTo(1002))
                 .body("[1].depName", equalTo("Sales"))
                 .body("[2].depId", equalTo(1003))
                 .body("[2].depName", equalTo("Technology"));
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testGetEmployeesInASpecificDepartment() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("depName", "Sales")
        .when()
				.get("/company/department/{depName}")
        .then()
                .log().everything()
                .statusCode(200)
                .body("[0].id", equalTo(100003))
                .body("[0].name", equalTo("Tracey"))
                .body("[1].id", equalTo(100008))
                .body("[1].name", equalTo("Patricia"))
                .body("[2].id", equalTo(100009))
                .body("[2].name", equalTo("Rachael"));
	}

	@Test
	@DatabaseSetup("/departmentTestData/ClearData.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testGetEmployeesInASpecificDepartment_whenTheDepartmentIsEmpty() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("depName", "Sales")
        .when()
				.get("/company/department/{depName}")
        .then()
                .log().everything()
                .statusCode(404);
	}

	@Test
	@DatabaseSetup("/departmentTestData/ClearData.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testReturnEmployeesByNumOfYearsWorked_whenTheyDoNotExist() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("number", 20)
        .when()
				.get("/company/employees/{number}")
		.then()
                .log().everything()
                .statusCode(404);

	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testReturnEmployeesByNumOfYearsWorked() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("number", 20)
        .when()
				.get("/company/employees/{number}")
        .then()
                .log().everything()
                .statusCode(200)
                .body("[0].id", equalTo(100001))
                .body("[0].name", equalTo("David"))
                .body("[1].id", equalTo(100002))
                .body("[1].name", equalTo("Kevin"));
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/departmentTestData/DepartmentsRemainedExpected.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testDeleteDepartment_whenExists() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("depName", "Sales")
        .when()
				.delete("/company/departments/{depName}")
        .then()
                .log().everything()
                .statusCode(200)
                .body("[0].depId", equalTo(1001))
                .body("[0].depName", equalTo("Research"))
                .body("[1].depId", equalTo(1003))
                .body("[1].depName", equalTo("Technology"))
                .body("[2].depId", equalTo(1004))
                .body("[2].depName", equalTo("Security"));
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testDeleteDepartment_whenDoesNotExists() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("depName", "Finance")
        .when()
				.delete("/company/departments/{depName}")
        .then()
                .statusCode(404);
	}

	@Test
	@DatabaseSetup("/departmentTestData/ClearData.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testDeleteDepartment_whenTheCompanyIsEmpty() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("depName", "Finance")
        .when()
				.delete("/company/departments/{depName}")
        .then()
                .statusCode(404);
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeesAfterDeleting.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testDeleteEmployee() {
		given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", 100014)
        .when()
				.delete("/company/employees/{id}")
        .then()
                .statusCode(200);
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testDeleteEmployee_whenDoesNotExist() {
		given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", 100017)
        .when()
				.delete("/company/employees/{id}")
        .then()
                .statusCode(404);
	}

	@Test
	@DatabaseSetup("/departmentTestData/ClearData.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testDeleteEmployee_whenTheCompanyIsEmpty() {
		given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", 100017)
        .when()
				.delete("/company/employees/{id}")
        .then()
                .statusCode(404);
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/departmentTestData/DepartmentAfterAdding.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testAddNewDepartment() throws JsonProcessingException {

		given()
                .contentType(APPLICATION_JSON)
                .body(getMockDepartmentRequestJSON())
        .when()
				.post("/company/department/")
        .then()
                .statusCode(201);
		}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testAddNewDepartment_whenIsDuplicate() throws JsonProcessingException {

		given()
                .contentType(APPLICATION_JSON)
                .body(getMockDuplicateDepartmentRequestJSON())
        .when()
				.post("/company/department/")
        .then()
                .statusCode(409);
		}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeeAfterAdding.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testAddNewEmployee() throws IOException {
		given()
                .contentType(APPLICATION_JSON)
                .body(getNewEmployeeDetailsRequestJSON())
        .when()
				.post("/company/employee")
        .then()
                .statusCode(201);
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testAddNewEmployee_employeeExists() throws JsonProcessingException {
		given()
                .contentType(APPLICATION_JSON)
                .body(getMockEmployeeDuplicateDetailsRequestJSON())
		.when()
                .post("/company/employee")
        .then()
                .statusCode(409);
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeeAfterUpdatingDetails.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testUpdateEmployeeJobTitle() throws JsonProcessingException {
		given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", 100014)
				.body(getMockEmployeeDetailsRequestJSON())
        .when()
                .put("/company/employee/{id}")
        .then()
                .statusCode(200);
	}

	@Test
	@DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/departmentTestData/ClearData.xml")
	public void testUpdateEmployeeJobTitle_whenDoesNotExist() throws JsonProcessingException {
		given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", 100017)
				.body(getMockEmployeeThatDoesNotExistDetailsRequestJSON())
        .when()
                .put("/company/employee/{id}")
        .then()
                .statusCode(404);
	}

	private String getNewEmployeeDetailsRequestJSON() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(anEmployee());
	}

    private Employee anEmployee() {
        EmployeeBuilder employeeBuilder = new EmployeeBuilder()
                .withDepartmentId(1003)
                .withHireDate(LocalDate.of(2016, Month.MAY, 29 ))
                .withId(100015)
                .withJobTitle("Tester")
                .withName("Alex")
                .withLname("dean")
                .withManageId(100004);
        return employeeBuilder.build();
    }

    private String getMockEmployeeDetailsRequestJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(anotherEmployee());

	}

    private Employee anotherEmployee() {
        EmployeeBuilder employeeBuilder = new EmployeeBuilder()
                .withDepartmentId(1004)
                .withHireDate(LocalDate.of(2010, Month.MAY, 9 ))
                .withId(100014)
                .withJobTitle("Account Manager")
                .withName("Andy")
                .withLname("McNee")
                .withManageId(100005);
        return employeeBuilder.build();
    }

    private String getMockEmployeeDuplicateDetailsRequestJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new EmployeeBuilder()
                .withDepartmentId(1003)
                .withHireDate(LocalDate.of(2013, Month.MARCH, 29 ))
                .withId(100012)
                .withJobTitle("Software Developer")
                .withName("Gerard")
                .withLname("Brawley")
                .withManageId(100004).build());

	}

	private String getMockEmployeeThatDoesNotExistDetailsRequestJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new EmployeeBuilder()
                .withDepartmentId(1003)
                .withHireDate(LocalDate.of(2014, Month.APRIL, 29 ))
                .withId(100017)
                .withJobTitle("PHP Developer")
                .withName("Gerrold")
                .withLname("Brown")
                .withManageId(100004).build());

	}

	private String getMockDepartmentRequestJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new DepartmentBuilder().withId(1005).withName("Finance").build());
	}

	private String getMockDuplicateDepartmentRequestJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(new DepartmentBuilder().withId(1002).withName("Sales").build());
	}

}

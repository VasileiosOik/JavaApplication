package com.connection.controller;

import com.connection.application.Application;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class ComapanyControllerIT {

	private static final String APPLICATION_JSON = "application/json";

	@LocalServerPort
	private int port;

	@Before
	public void setUp() {

		RestAssured.port = port;
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetAllEmployees() {

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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testReturnEmployeesByNumOfYearsWorked() {

		given()
                .contentType(APPLICATION_JSON)
                .pathParam("number", 20)
        .when()
				.get("/company/employees/{number}")
        .then()
                .log().everything()
                .statusCode(200)
                .body("[0].id", equalTo(100002))
                .body("[0].name", equalTo("Kevin"))
                .body("[1].id", equalTo(100003))
                .body("[1].name", equalTo("Tracey"));
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/DepartmentsRemainedExpected.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAfterDeleting.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/DepartmentAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewDepartment() {

		given()
                .contentType(APPLICATION_JSON)
                .body(getMockDepartmentRequestJSON())
        .when()
				.post("/company/department/")
        .then()
                .statusCode(201);
		}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewDepartment_whenIsDuplicate() {

		given()
                .contentType(APPLICATION_JSON)
                .body(getMockDuplicateDepartmentRequestJSON())
        .when()
				.post("/company/department/")
        .then()
                .statusCode(409);
		}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeeAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewEmployee() {
		given()
                .contentType(APPLICATION_JSON)
                .body(getNewEmployeeDetailsRequestJSON())
        .when()
				.post("/company/employee")
        .then()
                .statusCode(201);
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewEmployee_employeeExists() {
		given()
                .contentType(APPLICATION_JSON)
                .body(getMockEmployeeDuplicateDetailsRequestJSON())
		.when()
                .post("/company/employee")
        .then()
                .statusCode(409);
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeeAfterUpdatingDetails.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testUpdateEmployeeJobTitle() {
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
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testUpdateEmployeeJobTitle_whenDoesNotExist() {
		given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", 100017)
				.body(getMockEmployeeThatDoesNotExistDetailsRequestJSON())
        .when()
                .put("/company/employee/{id}")
        .then()
                .statusCode(404);
	}

	private String getNewEmployeeDetailsRequestJSON() {
		return "{" + "\"id\":100015," + "\"name\":\"Alex\"," + "\"jobTitle\":\"Tester\"," + "\"managerId\":100004,"
				+ "\"departmentId\":1003," + "\"lname\":\"Tso\"," + "\"hiredate\":\"2016-05-29\"" + " }";

	}

	private String getMockEmployeeDetailsRequestJSON() {
		return "{" + "\"id\":100014," + "\"name\":\"Andy\"," + "\"jobTitle\":\"Account Manager\","
				+ "\"managerId\":100005," + "\"departmentId\":1004," + "\"lname\":\"McNee\","
				+ "\"hiredate\":\"2010-05-09\"" + " }";

	}

	private String getMockEmployeeDuplicateDetailsRequestJSON() {
		return "{" + "\"id\":100012," + "\"name\":\"Gerard\"," + "\"jobTitle\":\"Software Developer\","
				+ "\"managerId\":100004," + "\"departmentId\":1003," + "\"lname\":\"Brawley\","
				+ "\"hiredate\":\"2013-03-29\"" + " }";

	}

	private String getMockEmployeeThatDoesNotExistDetailsRequestJSON() {
		return "{" + "\"id\":100017," + "\"name\":\"Gerrold\"," + "\"jobTitle\":\"PHP Developer\","
				+ "\"managerId\":100004," + "\"departmentId\":1003," + "\"lname\":\"Brown\","
				+ "\"hiredate\":\"2014-04-29\"" + " }";

	}

	private String getMockDepartmentRequestJSON() {
		return "{" + "\"depId\": \"1005\", " + "\"depName\": \"Finance\"" + " }";
	}

	private String getMockDuplicateDepartmentRequestJSON() {
		return "{" + "\"depId\": \"1002\", " + "\"depName\": \"Sales\"" + " }";
	}

}

package com.connection.controller;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.jayway.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.connection.controller.ConnectionController.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class ConnectionControllerIT {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionControllerIT.class);
	private static final String APPLICATION_JSON = "application/json";

	@LocalServerPort
	private int port;

	@Before
	public void setUp() {
		port = port;
	}

	@Before
	public void testRestAssuredController() {
		RestAssuredMockMvc.standaloneSetup(new ConnectionController());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetAllEmployees() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).when().get("/company/employees");
		JSONArray jsonArray = new JSONArray(response.body().asString());

		Assert.assertEquals(jsonArray.getJSONObject(0).getString("id"), "100001");
		Assert.assertEquals(jsonArray.getJSONObject(0).getString("name"), "David");

		Assert.assertEquals(jsonArray.getJSONObject(1).getString("id"), "100002");
		Assert.assertEquals(jsonArray.getJSONObject(1).getString("name"), "Kevin");

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetAllDepartments() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).when().get("/company/departments");
		JSONArray jsonArray = new JSONArray(response.body().asString());

		Assert.assertEquals(jsonArray.getJSONObject(0).getString("depId"), "1001");
		Assert.assertEquals(jsonArray.getJSONObject(0).getString("depName"), "Research");

		Assert.assertEquals(jsonArray.getJSONObject(1).getString("depId"), "1002");
		Assert.assertEquals(jsonArray.getJSONObject(1).getString("depName"), "Sales");

		Assert.assertEquals(jsonArray.getJSONObject(2).getString("depId"), "1003");
		Assert.assertEquals(jsonArray.getJSONObject(2).getString("depName"), "Technology");

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetEmployeesInASpecificDepartment() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Sales").when()
				.get("/company/department/{depName}");
		JSONArray jsonArray = new JSONArray(response.body().asString());

		Assert.assertEquals(jsonArray.getJSONObject(0).getString("id"), "100003");
		Assert.assertEquals(jsonArray.getJSONObject(0).getString("name"), "Tracey");

		Assert.assertEquals(jsonArray.getJSONObject(1).getString("id"), "100008");
		Assert.assertEquals(jsonArray.getJSONObject(1).getString("name"), "Patricia");

		Assert.assertEquals(jsonArray.getJSONObject(2).getString("id"), "100009");
		Assert.assertEquals(jsonArray.getJSONObject(2).getString("name"), "Rachael");

	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetEmployeesInASpecificDepartment_whenTheDepartmentIsEmpty() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Sales").when()
				.get("/company/department/{depName}");
		assertEquals(response.statusCode(), 404);

	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testReturnEmployeesByNumOfYearsWorked_whenTheyDoNotExist() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("number", 20).when()
				.get("/company/employees/{number}");
		assertEquals(response.statusCode(), 404);

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testReturnEmployeesByNumOfYearsWorked() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("number", 20).when()
				.get("/company/employees/{number}");
		JSONArray jsonArray = new JSONArray(response.body().asString());

		Assert.assertEquals(jsonArray.getJSONObject(0).getString("id"), "100002");
		Assert.assertEquals(jsonArray.getJSONObject(0).getString("name"), "Kevin");

		Assert.assertEquals(jsonArray.getJSONObject(1).getString("id"), "100003");
		Assert.assertEquals(jsonArray.getJSONObject(1).getString("name"), "Tracey");

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/DepartmentsRemainedExpected.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteDepartment_whenExists() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Sales").when()
				.delete("/company/departments/{depName}");
		if (response.statusCode() == 200) {

			JSONArray jsonArray = new JSONArray(response.body().asString());
			Assert.assertEquals(jsonArray.length(), 3);

			Assert.assertEquals(jsonArray.getJSONObject(0).getString("depId"), "1001");
			Assert.assertEquals(jsonArray.getJSONObject(0).getString("depName"), "Research");

			Assert.assertEquals(jsonArray.getJSONObject(1).getString("depId"), "1003");
			Assert.assertEquals(jsonArray.getJSONObject(1).getString("depName"), "Technology");

			Assert.assertEquals(jsonArray.getJSONObject(2).getString("depId"), "1004");
			Assert.assertEquals(jsonArray.getJSONObject(2).getString("depName"), "Security");
		}
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteDepartment_whenDoesNotExists() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Finance").when()
				.delete("/company/departments/{depName}");
		assertEquals(response.statusCode(), 404);
	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteDepartment_whenTheCompanyIsEmpty() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Finance").when()
				.delete("/company/departments/{depName}");
		assertEquals(response.statusCode(), 204);
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAfterDeleting.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteEmployee() throws Exception {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100014).when()
				.delete("/company/employees/{id}");

		Assert.assertEquals(response.getStatusCode(), 200);

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteEmployee_whenDoesNotExist() throws Exception {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100017).when()
				.delete("/company/employees/{id}");

		Assert.assertEquals(response.getStatusCode(), 404);

	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteEmployee_whenTheCompanyIsEmpty() throws Exception {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100017).when()
				.delete("/company/employees/{id}");

		Assert.assertEquals(response.getStatusCode(), 204);

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/DepartmentAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewDepartment() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).body(getMockDepartmentRequestJSON()).when()
				.post("/company/department/");

		Assert.assertEquals(response.getStatusCode(), 201);
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewDepartment_whenIsDuplicate() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).body(getMockDuplicateDepartmentRequestJSON()).when()
				.post("/company/department/");

		Assert.assertEquals(response.getStatusCode(), 409);
	}

	// --------------add a new employee---------------
	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeeAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewEmployee() {
		Response response = given().contentType(APPLICATION_JSON).body(getNewEmployeeDetailsRequestJSON()).when()
				.post("/company/employee");

		Assert.assertEquals(response.getStatusCode(), 201);

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewEmployee_employeeExists() {
		Response response = given().contentType(APPLICATION_JSON).body(getMockEmployeeDuplicateDetailsRequestJSON())
				.when().post("/company/employee");

		Assert.assertEquals(response.getStatusCode(), 409);

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeeAfterUpdatingDetails.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testUpdateEmployeeJobTitle() throws JSONException {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100014)
				.body(getMockEmployeeDetailsRequestJSON()).when().put("/company/employee/{id}");

		Assert.assertEquals(response.getStatusCode(), 200);

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testUpdateEmployeeJobTitle_whenDoesNotExist() throws JSONException {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100017)
				.body(getMockEmployeeThatDoesNotExistDetailsRequestJSON()).when().put("/company/employee/{id}");

		Assert.assertEquals(response.getStatusCode(), 404);

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

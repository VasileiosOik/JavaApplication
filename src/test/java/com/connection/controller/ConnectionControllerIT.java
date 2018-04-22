package com.connection.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import com.jayway.restassured.response.Response;
import org.json.JSONArray;
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

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

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

		RestAssured.port = port;
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

		Assert.assertEquals("100001", jsonArray.getJSONObject(0).getString("id"));
		Assert.assertEquals("David", jsonArray.getJSONObject(0).getString("name"));

		Assert.assertEquals("100002", jsonArray.getJSONObject(1).getString("id"));
		Assert.assertEquals("Kevin", jsonArray.getJSONObject(1).getString("name"));

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetAllDepartments() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).when().get("/company/departments");
		JSONArray jsonArray = new JSONArray(response.body().asString());

		Assert.assertEquals("1001", jsonArray.getJSONObject(0).getString("depId"));
		Assert.assertEquals("Research", jsonArray.getJSONObject(0).getString("depName"));

		Assert.assertEquals("1002", jsonArray.getJSONObject(1).getString("depId"));
		Assert.assertEquals("Sales", jsonArray.getJSONObject(1).getString("depName"));

		Assert.assertEquals("1003", jsonArray.getJSONObject(2).getString("depId"));
		Assert.assertEquals("Technology", jsonArray.getJSONObject(2).getString("depName"));

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetEmployeesInASpecificDepartment() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Sales").when()
				.get("/company/department/{depName}");
		JSONArray jsonArray = new JSONArray(response.body().asString());

		Assert.assertEquals("100003", jsonArray.getJSONObject(0).getString("id"));
		Assert.assertEquals("Tracey", jsonArray.getJSONObject(0).getString("name"));

		Assert.assertEquals("100008", jsonArray.getJSONObject(1).getString("id"));
		Assert.assertEquals("Patricia", jsonArray.getJSONObject(1).getString("name"));

		Assert.assertEquals("100009", jsonArray.getJSONObject(2).getString("id"));
		Assert.assertEquals("Rachael", jsonArray.getJSONObject(2).getString("name"));

	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetEmployeesInASpecificDepartment_whenTheDepartmentIsEmpty() {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Sales").when()
				.get("/company/department/{depName}");
		assertEquals(404, response.statusCode());

	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testReturnEmployeesByNumOfYearsWorked_whenTheyDoNotExist() {

		Response response = given().contentType(APPLICATION_JSON).pathParam("number", 20).when()
				.get("/company/employees/{number}");
		assertEquals(404, response.statusCode());

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testReturnEmployeesByNumOfYearsWorked() throws Exception {

		Response response = given().contentType(APPLICATION_JSON).pathParam("number", 20).when()
				.get("/company/employees/{number}");
		JSONArray jsonArray = new JSONArray(response.body().asString());

		Assert.assertEquals("100002", jsonArray.getJSONObject(0).getString("id"));
		Assert.assertEquals("Kevin", jsonArray.getJSONObject(0).getString("name"));

		Assert.assertEquals("100003", jsonArray.getJSONObject(1).getString("id"));
		Assert.assertEquals("Tracey", jsonArray.getJSONObject(1).getString("name"));

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
			Assert.assertEquals(3, jsonArray.length());

			Assert.assertEquals("1001", jsonArray.getJSONObject(0).getString("depId"));
			Assert.assertEquals("Research", jsonArray.getJSONObject(0).getString("depName"));

			Assert.assertEquals("1003", jsonArray.getJSONObject(1).getString("depId"));
			Assert.assertEquals("Technology", jsonArray.getJSONObject(1).getString("depName"));

			Assert.assertEquals("1004", jsonArray.getJSONObject(2).getString("depId"));
			Assert.assertEquals("Security", jsonArray.getJSONObject(2).getString("depName"));
		}
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteDepartment_whenDoesNotExists() {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Finance").when()
				.delete("/company/departments/{depName}");
		assertEquals(404, response.statusCode());
	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteDepartment_whenTheCompanyIsEmpty() {

		Response response = given().contentType(APPLICATION_JSON).pathParam("depName", "Finance").when()
				.delete("/company/departments/{depName}");
		assertEquals(204, response.statusCode());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAfterDeleting.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteEmployee() {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100014).when()
				.delete("/company/employees/{id}");

		Assert.assertEquals(200, response.getStatusCode());

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteEmployee_whenDoesNotExist() {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100017).when()
				.delete("/company/employees/{id}");

		Assert.assertEquals(404, response.getStatusCode());

	}

	@Test
	@DatabaseSetup("/ClearData.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testDeleteEmployee_whenTheCompanyIsEmpty() {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100017).when()
				.delete("/company/employees/{id}");

		Assert.assertEquals(204, response.getStatusCode());

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/DepartmentAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewDepartment() {

		Response response = given().contentType(APPLICATION_JSON).body(getMockDepartmentRequestJSON()).when()
				.post("/company/department/");

		Assert.assertEquals(201, response.getStatusCode());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewDepartment_whenIsDuplicate() {

		Response response = given().contentType(APPLICATION_JSON).body(getMockDuplicateDepartmentRequestJSON()).when()
				.post("/company/department/");

		Assert.assertEquals(409, response.getStatusCode());
	}

	// --------------add a new employee---------------
	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeeAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewEmployee() {
		Response response = given().contentType(APPLICATION_JSON).body(getNewEmployeeDetailsRequestJSON()).when()
				.post("/company/employee");

		Assert.assertEquals(201, response.getStatusCode());

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddNewEmployee_employeeExists() {
		Response response = given().contentType(APPLICATION_JSON).body(getMockEmployeeDuplicateDetailsRequestJSON())
				.when().post("/company/employee");

		Assert.assertEquals(409, response.getStatusCode());

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeeAfterUpdatingDetails.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testUpdateEmployeeJobTitle() {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100014)
				.body(getMockEmployeeDetailsRequestJSON()).when().put("/company/employee/{id}");

		Assert.assertEquals(200, response.getStatusCode());

	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testUpdateEmployeeJobTitle_whenDoesNotExist() {
		Response response = given().contentType(APPLICATION_JSON).pathParam("id", 100017)
				.body(getMockEmployeeThatDoesNotExistDetailsRequestJSON()).when().put("/company/employee/{id}");

		Assert.assertEquals(404, response.getStatusCode());

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

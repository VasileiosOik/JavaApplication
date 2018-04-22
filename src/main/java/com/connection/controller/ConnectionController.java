package com.connection.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.connection.dao.ConnectionDAO;
import com.connection.dao.MongoDAO;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.customerror.CustomErrorType;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@RestController
@ComponentScan("com.connection.*")
@RequestMapping("/company")
@SwaggerDefinition(tags = { @Tag(name = "company", description = "Operations pertaining to manage a company") })
public class ConnectionController {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionController.class);

	@Autowired
	private ConnectionDAO connectionDAO;

	@Autowired
	private MongoDAO mongoDAO;

	@ApiOperation(value = "View the list of available Employees", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 204, message = "No content to display") })
	@RequestMapping(value = "/employees", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Employee>> returnAllEmployees() {
		List<Employee> employees = connectionDAO.getAllEmployees();

		if (employees.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(employees, HttpStatus.OK);
	}

	@ApiOperation(value = "View the list of available Departments", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 204, message = "No content to display") })
	@RequestMapping(value = "/departments", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Department>> returnAllDepartments() {
		List<Department> departments = connectionDAO.getAllDepartments();

		if (departments.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(departments, HttpStatus.OK);
	}

	@ApiOperation(value = "View the list of available employees in a specific department", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 404, message = "No content to display") })
	@RequestMapping(value = "/department/{depName}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getEmployeesInASpecificDepartment(@PathVariable("depName") String depName) {
		LOG.info("Fetching Employees that work in the {} department", depName);
		List<Employee> employees = connectionDAO.getAllEmployeesInADepartment(depName);
		if (employees.isEmpty()) {
			LOG.error("Employees in the department with this name {} do not exist.", depName);
			return new ResponseEntity<>(
					new CustomErrorType("Employees in the department " + depName + " cannot be found"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(employees, HttpStatus.OK);
	}

	@ApiOperation(value = "View the list of employees that have worked for this number of years", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 404, message = "No content to display") })
	@RequestMapping(value = "/employees/{number}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> returnEmployeesByNumOfYearsWorked(@PathVariable("number") int number) {
		LOG.info("Fetching Employees with {} years of employment ", number);
		List<Employee> employees = connectionDAO.getEmployeesByNumOfYearsWorked(number);
		if (employees.isEmpty()) {
			LOG.error("Employees that have worked {} years not found.", number);
			return new ResponseEntity<>(new CustomErrorType("Employees with " + number + "of years not found"),
					HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(employees, HttpStatus.OK);
	}

	@ApiOperation(value = "Delete a department", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted"),
			@ApiResponse(code = 204, message = "No content to delete") })
	@RequestMapping(value = "/departments/{depName}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteDepartment(@PathVariable("depName") String depName) {
		LOG.info("Fetching and Deleting Department with name {}", depName);

		List<Department> departments = connectionDAO.getAllDepartments();

		if (!departments.isEmpty()) {
			for (Department dep : departments) {

				if (dep.getDepName().equals(depName)) {
					LOG.info("Department with {} name found", depName);
					connectionDAO.removeDepartment(depName);
					return new ResponseEntity<>(connectionDAO.getAllDepartments(), HttpStatus.OK);
				}
			}
		} else {
			LOG.debug("No department content to delete");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		LOG.error("Unable to delete. Department with name {} not found.", depName);
		return new ResponseEntity<>(
				new CustomErrorType("Unable to delete. Department with name " + depName + " is not there."),
				HttpStatus.NOT_FOUND);
	}

	@ApiOperation(value = "Delete an Employee", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted"),
			@ApiResponse(code = 204, message = "No content to delete") })
	@RequestMapping(value = "/employees/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteEmployee(@PathVariable("id") int id) {
		LOG.info("Fetching and Deleting employee with id {}", id);

		List<Employee> employees = connectionDAO.getAllEmployees();

		if (!employees.isEmpty()) {

			for (Employee emp : employees) {

				if (emp.getId() == (id)) {
					LOG.info("Employee with {} id found", id);
					connectionDAO.removeEmployee(emp.getName(), emp.getLname());
					return new ResponseEntity<>(connectionDAO.getAllDepartments(), HttpStatus.OK);
				}
			}
		} else {
			LOG.debug("No employee content to delete");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		LOG.error("Unable to delete. Employee with id {} not found.", id);
		return new ResponseEntity<>(new CustomErrorType("Unable to delete. Employee with id " + id + " not found."),
				HttpStatus.NOT_FOUND);
	}

	@ApiOperation(value = "Add a new department", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successfully created"),
			@ApiResponse(code = 409, message = "Already exists") })
	@RequestMapping(value = "/department", method = RequestMethod.POST)
	public ResponseEntity<Object> addNewDepartment(@RequestBody Department department, UriComponentsBuilder ucBuilder) {
		LOG.info("Creating Department : {}", department);

		if (connectionDAO.verifyDepartmentExistence(department.getDepName()) == 0) {
			LOG.error("Unable to create. A department with name {} already exist", department.getDepName());
			return new ResponseEntity<>(
					new CustomErrorType(
							"Unable to create. A department with name " + department.getDepName() + " already exist."),
					HttpStatus.CONFLICT);
		}
		connectionDAO.addDepartment(department);
		mongoDAO.addDepartmentToMongoDB(department);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/company/department").buildAndExpand(department.getDepId()).toUri());
		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@ApiOperation(value = "Update the job title of an employee", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully updated"),
			@ApiResponse(code = 404, message = "Not found") })
	@RequestMapping(value = "/employee/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> updateEmployeeJobTitle(@PathVariable("id") int id, @RequestBody Employee employee) {

		Employee currentEmployee = connectionDAO.verifyEmployeeExistence(id);

		if (currentEmployee == null) {
			LOG.error("Unable to update. Employee with id {} not found.", id);
			return new ResponseEntity<>(new CustomErrorType("Unable to update. Employee with id " + id + " not found."),
					HttpStatus.NOT_FOUND);
		}
		LOG.info("Updating Employee with id {}", id);
		currentEmployee.setJobTitle(employee.getJobTitle());

		connectionDAO.changeEmployeeJobTitle(employee.getName(), employee.getLname(), currentEmployee.getJobTitle());
		return new ResponseEntity<>(currentEmployee, HttpStatus.OK);
	}

	// new employee
	@ApiOperation(value = "Add a new employee", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successfully created"),
			@ApiResponse(code = 409, message = "Already exists") })
	@RequestMapping(value = "/employee", method = RequestMethod.POST)
	public ResponseEntity<Object> addNewEmployee(@RequestBody Employee employee, UriComponentsBuilder ucBuilder) {

		if (connectionDAO.verifyEmployeeExistence(employee.getId()) != null) {
			LOG.error("Unable to create.");
			return new ResponseEntity<>(
					new CustomErrorType(
							"Unable to create. An employee with name " + employee.getName() + " already exist."),
					HttpStatus.CONFLICT);
		}
		connectionDAO.addEmployee(employee);
		mongoDAO.addEmployeeToMongoDB(employee);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/company/employee").buildAndExpand(employee.getId()).toUri());
		return new ResponseEntity<>(headers, HttpStatus.CREATED);

	}

}

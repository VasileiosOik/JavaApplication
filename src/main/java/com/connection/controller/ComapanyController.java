package com.connection.controller;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.service.CompanyService;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/company")
@SwaggerDefinition(tags = { @Tag(name = "company", description = "Operations pertaining to manage a company") })
public class ComapanyController {

	private final CompanyService companyService;

	public ComapanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@ApiOperation(value = "View the list of available Employees", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 204, message = "No content to display") })
	@RequestMapping(value = "/employees", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Employee>> returnAllEmployees() {
		return companyService.returnAllEmployees();
	}

	@ApiOperation(value = "View the list of available Departments", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 204, message = "No content to display") })
	@RequestMapping(value = "/departments", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Department>> returnAllDepartments() {
		return companyService.returnAllDepartments();
	}

	@ApiOperation(value = "View the list of available employees in a specific department", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 404, message = "No content to display") })
	@RequestMapping(value = "/department/{depName}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getEmployeesInASpecificDepartment(@PathVariable("depName") String depName) {
		return companyService.getEmployeesInASpecificDepartment(depName);
	}

	@ApiOperation(value = "View the list of employees that have worked for this number of years", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 404, message = "No content to display") })
	@RequestMapping(value = "/employees/{number}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> returnEmployeesByNumOfYearsWorked(@PathVariable("number") int number) {
	    return companyService.returnEmployeesByNumOfYearsWorked(number);
	}

	@ApiOperation(value = "Delete a department", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted"),
			@ApiResponse(code = 404, message = "No department found to delete") })
	@RequestMapping(value = "/departments/{depName}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteDepartment(@PathVariable("depName") String depName) {
		return companyService.deleteDepartment(depName);
	}

	@ApiOperation(value = "Delete an Employee", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully deleted"),
			@ApiResponse(code = 404, message = "No employee found to delete") })
	@RequestMapping(value = "/employees/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteEmployee(@PathVariable("id") int id) {
		return companyService.deleteEmployee(id);
	}

	@ApiOperation(value = "Add a new department", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successfully created"),
			@ApiResponse(code = 409, message = "Already exists") })
	@RequestMapping(value = "/department", method = RequestMethod.POST)
	public ResponseEntity<Object> addNewDepartment(@RequestBody Department department, UriComponentsBuilder ucBuilder) {
		return companyService.addNewDepartment(department, ucBuilder);
	}

	@ApiOperation(value = "Update the job title of an employee", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully updated"),
			@ApiResponse(code = 404, message = "Not found") })
	@RequestMapping(value = "/employee/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> updateEmployeeJobTitle(@PathVariable("id") int id, @RequestBody Employee employee) {

		return companyService.updateEmployeeJobTitle(id, employee);
	}

	@ApiOperation(value = "Add a new employee", response = ResponseEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Successfully created"),
			@ApiResponse(code = 409, message = "Already exists") })
	@RequestMapping(value = "/employee", method = RequestMethod.POST)
	public ResponseEntity<Object> addNewEmployee(@RequestBody Employee employee, UriComponentsBuilder ucBuilder) {
		return companyService.addNewEmployee(employee, ucBuilder);
	}

}

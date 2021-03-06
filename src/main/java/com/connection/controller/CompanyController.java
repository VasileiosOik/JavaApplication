package com.connection.controller;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.service.CompanyService;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/company")
@SwaggerDefinition(tags = {@Tag(name = "company", description = "Operations pertaining to manage a company")})
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @ApiOperation(value = "View the list of available Employees", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "No content to display")})
    @GetMapping(value = "/employees", produces = "application/json")
    public List<Employee> getEmployees() {
        return companyService.getEmployees();
    }

    @ApiOperation(value = "View the list of available Departments", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "No content to display")})
    @GetMapping(value = "/departments", produces = "application/json")
    public List<Department> getDepartments() {
        return companyService.getDepartments();
    }

    @ApiOperation(value = "View the list of available employees in a specific department", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "No content to display")})
    @GetMapping(value = "/department/{depName}", produces = "application/json")
    public List<Employee> getEmployeesInASpecificDepartment(@PathVariable("depName") String depName) {
        return companyService.getEmployeesInASpecificDepartment(depName);
    }

    @ApiOperation(value = "View the list of employees that have worked for this number of years", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "No content to display")})
    @GetMapping(value = "/employees/{number}", produces = "application/json")
    public List<Employee> getEmployeesByNumOfYearsWorked(@PathVariable("number") int number) {
        return companyService.getEmployeesByNumOfYearsWorked(number);
    }

    @ApiOperation(value = "Delete a department", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully deleted"),
            @ApiResponse(code = 404, message = "No department found to delete")})
    @DeleteMapping(value = "/departments/{depName}")
    public List<Department> deleteDepartment(@PathVariable("depName") String depName) {
        return companyService.deleteDepartment(depName);
    }

    @ApiOperation(value = "Delete an Employee", response = List.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully deleted"),
            @ApiResponse(code = 404, message = "No employee found to delete")})
    @DeleteMapping(value = "/employees/{id}")
    public List<Employee> deleteEmployee(@PathVariable("id") int id) {
        return companyService.deleteEmployee(id);
    }

    @ApiOperation(value = "Add a new department", response = Department.class)
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Successfully created"),
            @ApiResponse(code = 409, message = "Already exists")})
    @PostMapping(value = "/department", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Department addDepartment(@RequestBody Department department, UriComponentsBuilder ucBuilder) {
        return companyService.addDepartment(department, ucBuilder);
    }

    @ApiOperation(value = "Update the job title of an employee", response = Employee.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully updated"),
            @ApiResponse(code = 404, message = "Not found")})
    @PutMapping(value = "/employee/{id}")
    public Employee updateEmployeeJobTitle(@PathVariable("id") int id, @RequestBody Employee employee) {
        return companyService.updateEmployeeJobTitle(id, employee);
    }

    @ApiOperation(value = "Add a new employee", response = Employee.class)
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Successfully created"),
            @ApiResponse(code = 409, message = "Already exists")})
    @PostMapping(value = "/employee", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Employee addEmployee(@RequestBody Employee employee, UriComponentsBuilder ucBuilder) {
        return companyService.addEmployee(employee, ucBuilder);
    }

    @ApiOperation(value = "Get an Employee by searching with id", response = Employee.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved"),
            @ApiResponse(code = 404, message = "Not found")})
    @GetMapping(value = "/oneemployee/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee getAnEmployeeById(@PathVariable("id") int id) {
        return companyService.getAnEmployeeById(id);
    }

    @ApiOperation(value = "Update Employee information", response = Employee.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully updated"),
            @ApiResponse(code = 404, message = "Not found")})
    @PutMapping(value = "/updateemployee/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Employee updateAnEmployee(@PathVariable("id") int id, @RequestBody Employee employee) {
        return companyService.updateAnEmployee(id, employee);
    }

}

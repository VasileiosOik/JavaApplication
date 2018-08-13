package com.connection.service;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public interface CompanyService {

    ResponseEntity<List<Employee>> returnAllEmployees();

    ResponseEntity<List<Department>> returnAllDepartments();

    ResponseEntity<Object> getEmployeesInASpecificDepartment(String depName);

    ResponseEntity<Object> returnEmployeesByNumOfYearsWorked(int number);

    ResponseEntity<Object> deleteDepartment(String depName);

    ResponseEntity<Object> deleteEmployee(int id);

    ResponseEntity<Object> addNewDepartment(Department department, UriComponentsBuilder ucBuilder);

    ResponseEntity<Object> updateEmployeeJobTitle(int id, Employee employee);

    ResponseEntity<Object> addNewEmployee(Employee employee, UriComponentsBuilder ucBuilder);

    ResponseEntity<Object> changeAnEmployeeDepartment(String name, String lName, String departmentName);

    ResponseEntity<Object> getAnEmployee(int id);

    ResponseEntity<Object> updateAnEmployee(int id, Employee employee);
}

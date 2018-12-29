package com.connection.service;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public interface CompanyService {

    ResponseEntity<List<Employee>> getEmployees();

    ResponseEntity<List<Department>> getDepartments();

    ResponseEntity<Object> getEmployeesInASpecificDepartment(String depName);

    ResponseEntity<Object> getEmployeesByNumOfYearsWorked(int number);

    ResponseEntity<Object> deleteDepartment(String depName);

    ResponseEntity<Object> deleteEmployee(int id);

    ResponseEntity<Object> addDepartment(Department department, UriComponentsBuilder ucBuilder);

    ResponseEntity<Object> updateEmployeeJobTitle(int id, Employee employee);

    ResponseEntity<Object> addEmployee(Employee employee, UriComponentsBuilder ucBuilder);

    void changeAnEmployeeDepartment(String name, String lName, String departmentName);

    ResponseEntity<Object> getAnEmployeeById(int id);

    ResponseEntity<Object> updateAnEmployee(int id, Employee employee);
}

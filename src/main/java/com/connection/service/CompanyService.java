package com.connection.service;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public interface CompanyService {

    List<Employee> getEmployees();

    List<Department> getDepartments();

    List<Employee> getEmployeesInASpecificDepartment(String depName);

    List<Employee> getEmployeesByNumOfYearsWorked(int number);

    List<Department> deleteDepartment(String depName);

    List<Employee> deleteEmployee(int id);

    Department addDepartment(Department department, UriComponentsBuilder ucBuilder);

    Employee updateEmployeeJobTitle(int id, Employee employee);

    Employee addEmployee(Employee employee, UriComponentsBuilder ucBuilder);

    void changeAnEmployeeDepartment(String name, String lName, String departmentName);

    Employee getAnEmployeeById(int id);

    Employee updateAnEmployee(int id, Employee employee);
}

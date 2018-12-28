package com.connection.dao;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyDao {

    private final CompanyMapper companyMapper;

    @Autowired
    public CompanyDao(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }


    public List<Employee> getEmployees() {
        return companyMapper.showAllEmployees();
    }

    public List<Department> getDepartments() {
        return companyMapper.showAllDepartments();
    }

    public List<Employee> getEmployeesInASpecificDepartment(String departmentName) {
        return companyMapper.employeesInSpecificDepartment(departmentName);
    }

    public List<Employee> getEmployeesByNumOfYearsWorked(int workedYears) {
        return companyMapper.getEmployeesByNumOfYearsWorked(workedYears);
    }

    public void deleteDepartment(String departmentName) {
        companyMapper.removeDepartment(departmentName);
    }

    public void updateEmployeeDepartmentId(String departmentName) {
        companyMapper.updateEmployeeDepartmentId(departmentName);
    }

    public Department verifyDepartmentExistence(String departmentName) {
        return companyMapper.verifyDepartmentExistence(departmentName);
    }

    public Employee verifyEmployeeExistence(int id) {
        return companyMapper.verifyEmployeeExistence(id);
    }

    public int deleteEmployee(int id) {
        return companyMapper.removeEmployee(id);
    }

    public void addDepartment(Department department) {
        companyMapper.addDepartment(department);
    }

    public void changeEmployeeJobTitle(String name, String lastName, String jobTitle) {
        companyMapper.changeEmployeeJobTitle(name, lastName, jobTitle);
    }

    public void addEmployee(Employee employee) {
        companyMapper.addEmployee(employee);
    }

    public void changeAnEmployeeDepartmentAndCheckIfManager(String name, String lastName, String departmentName) {
        companyMapper.changeAnEmployeeDepartmentAndCheckIfManager(name, lastName, departmentName);
    }

    public List<Employee> changeAnEmployeeDepartment(String name, String lastName, String departmentName) {
        return companyMapper.changeAnEmployeeDepartment(name, lastName, departmentName);
    }

    public Employee getAnEmployeeById(int id) {
        return companyMapper.getAnEmployee(id);
    }

    public void updateAnEmployee(int id, Employee employee) {
        companyMapper.updateAnEmployee(id, employee);
    }
}

package com.connection.dao;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CompanyDao {

    private final CompanyMapper companyMapper;

    @Autowired
    public CompanyDao(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    @Cacheable(value = "allEmployeesCache", unless = "#result.size() == 0")
    public List<Employee> getEmployees() {
        return companyMapper.getAllEmployees();
    }

    public List<Department> getDepartments() {
        return companyMapper.getAllDepartments();
    }

    public List<Employee> getEmployeesInASpecificDepartment(String departmentName) {
        return companyMapper.getEmployeesInSpecificDepartment(departmentName);
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

    @Caching(
            evict = {
                    @CacheEvict(value = "employeeCache", key = "#id"),
                    @CacheEvict(value = "allEmployeesCache", allEntries = true)
            }
    )
    public int deleteEmployee(int id) {
        return companyMapper.removeEmployee(id);
    }

    public void addDepartment(Department department) {
        companyMapper.addDepartment(department);
    }

    public void changeEmployeeJobTitle(String name, String lastName, String jobTitle) {
        companyMapper.changeEmployeeJobTitle(name, lastName, jobTitle);
    }

    @Caching(
            put = {@CachePut(value = "employeeCache", key = "#employee.id")},
            evict = {@CacheEvict(value = "allEmployeesCache", allEntries = true)}
    )
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

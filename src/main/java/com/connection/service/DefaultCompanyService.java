package com.connection.service;

import com.connection.customerror.CustomErrorType;
import com.connection.dao.CompanyMongoDao;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.CompanyMapper;
import com.connection.publisher.ActionMessagePublisher;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
public class DefaultCompanyService implements CompanyService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCompanyService.class);

    private final CompanyMongoDao companyMongoDao;

    private final CompanyMapper companyMapper;

    private final ActionMessagePublisher actionMessagePublisher;


    @Autowired
    public DefaultCompanyService(CompanyMongoDao companyMongoDao, CompanyMapper companyMapper, ActionMessagePublisher actionMessagePublisher) {
        this.companyMongoDao = companyMongoDao;
        this.companyMapper = companyMapper;
        this.actionMessagePublisher = actionMessagePublisher;
    }


    @Override
    public ResponseEntity<List<Employee>> returnAllEmployees() {
        List<Employee> employees = companyMapper.showAllEmployees();

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Department>> returnAllDepartments() {
        List<Department> departments = companyMapper.showAllDepartments();

        if (CollectionUtils.isEmpty(departments)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getEmployeesInASpecificDepartment(String depName) {
        LOG.info("Fetching Employees that work in the {} department", depName);
        List<Employee> employees = companyMapper.employeesInSpecificDepartment(depName);

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(
                    new CustomErrorType("Employees in the department " + depName + " cannot be found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> returnEmployeesByNumOfYearsWorked(int number) {
        LOG.info("Fetching Employees with {} years of employment ", number);
        List<Employee> employees = companyMapper.getEmployeesByNumOfYearsWorked(number);

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(new CustomErrorType("Employees with " + number + "of years worked not found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteDepartment(String depName) {
        LOG.info("Fetching and Deleting Department with name {}", depName);

        if (companyMapper.verifyDepartmentExistence(depName) != null) {
            // because of the foreign keys constrains first you remove the
            // department and second you remove the employee
            companyMapper.updateEmployee(depName);
            companyMapper.removeDepartment(depName);
            LOG.info("Department with {} name found", depName);
            return new ResponseEntity<>(companyMapper.showAllDepartments(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new CustomErrorType("Unable to delete. Department with name " + depName + " is not there."),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> deleteEmployee(int id) {
        LOG.info("Fetching and Deleting employee with id {}", id);

        if (companyMapper.removeEmployee(id) > 0) {
            LOG.info("Employee with {} id found", id);
            return new ResponseEntity<>(companyMapper.showAllEmployees(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomErrorType("Unable to delete employee with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> addNewDepartment(Department department, UriComponentsBuilder ucBuilder) {
        LOG.info("Creating Department : {}", department);

        if (companyMapper.verifyDepartmentExistence(department.getDepName()) != null) {
            LOG.debug("Unable to create. A department with name {} already exist", department.getDepName());
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "Unable to create. A department with name " + department.getDepName() + " already exist."),
                    HttpStatus.CONFLICT);
        }
        LOG.debug("The department is: {}", department);
        companyMapper.addDepartment(department);
        companyMongoDao.addDepartmentToMongoDB(department);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/company/department").buildAndExpand(department.getDepId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> updateEmployeeJobTitle(int id, Employee employee) {
        Employee currentEmployee = companyMapper.verifyIfEmployeeExists(id);

        if (currentEmployee == null) {
            LOG.debug("Unable to update. Employee with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Unable to update. Employee with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        LOG.info("Updating Employee with id {}", id);
        currentEmployee.setJobTitle(employee.getJobTitle());

        companyMapper.changeEmployeeJobTitle(employee.getName(), employee.getlName(), currentEmployee.getJobTitle());
        return new ResponseEntity<>(currentEmployee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> addNewEmployee(Employee employee, UriComponentsBuilder ucBuilder) {

        if (companyMapper.verifyIfEmployeeExists(employee.getId()) != null) {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "Unable to create. An employee with id " + employee.getId() + " already exist."),
                    HttpStatus.CONFLICT);
        }
        LOG.debug("The employee is: {}", employee);
        companyMapper.addEmployee(employee);
        companyMongoDao.addEmployeeToMongoDB(employee);
        actionMessagePublisher.publish(employee);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/company/employee").buildAndExpand(employee.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> changeAnEmployeeDepartment(String name, String lName, String departmentName) {
        List<Employee> listEmp = companyMapper.changeAnEmployeeDepartment(name, lName, departmentName);

        if (CollectionUtils.isEmpty(listEmp)) {
            companyMapper.changeAnEmployeeDepartmentAndCheckIfManager(name, lName, departmentName);
            LOG.debug("Employee data has been updated successfully!");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "The current employee is a manager and cannot be transferred"),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> getAnEmployee(int id) {
        Employee anEmployee = companyMapper.getAnEmployee(id);
        if (null != anEmployee) {
            LOG.debug("Retrieved einai: [{}]", anEmployee);
            return new ResponseEntity<>(anEmployee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "The current employee cannot be found"),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> updateAnEmployee(int id, Employee employee) {
        try {
            companyMapper.updateAnEmployee(id, employee);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "The current employee cannot be found"),
                    HttpStatus.NOT_FOUND);
        }
    }
}

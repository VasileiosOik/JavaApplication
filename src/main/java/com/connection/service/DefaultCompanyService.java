package com.connection.service;

import com.connection.customexception.CustomErrorType;
import com.connection.dao.CompanyDao;
import com.connection.dao.CompanyEventDao;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.publisher.CompanyMessagePublisher;
import com.connection.validation.EmployeeValidator;
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

    private final CompanyEventDao companyEventDao;

    private final CompanyDao companyDao;

    private final CompanyMessagePublisher companyMessagePublisher;

    private final EmployeeValidator employeeValidator;


    @Autowired
    public DefaultCompanyService(CompanyEventDao companyEventDao, CompanyDao companyDao, CompanyMessagePublisher companyMessagePublisher, EmployeeValidator employeeValidatorvalidator) {
        this.companyEventDao = companyEventDao;
        this.companyDao = companyDao;
        this.companyMessagePublisher = companyMessagePublisher;
        this.employeeValidator = employeeValidatorvalidator;
    }


    @Override
    public ResponseEntity<List<Employee>> getEmployees() {
        List<Employee> employees = companyDao.getEmployees();

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Department>> getDepartments() {
        List<Department> departments = companyDao.getDepartments();

        if (CollectionUtils.isEmpty(departments)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getEmployeesInASpecificDepartment(String depName) {
        LOG.info("Fetching Employees that work in the {} department", depName);
        List<Employee> employees = companyDao.getEmployeesInASpecificDepartment(depName);

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(
                    new CustomErrorType("Employees in the department " + depName + " cannot be found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getEmployeesByNumOfYearsWorked(int number) {
        LOG.info("Fetching Employees with {} years of employment ", number);
        List<Employee> employees = companyDao.getEmployeesByNumOfYearsWorked(number);

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(new CustomErrorType("Employees with " + number + "of years worked not found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteDepartment(String depName) {
        LOG.info("Fetching and Deleting Department with name {}", depName);

        if (companyDao.verifyDepartmentExistence(depName) != null) {
            // because of the foreign keys constrains first you remove the
            // department and second you remove the employee
            companyDao.updateEmployeeDepartmentId(depName);
            companyDao.deleteDepartment(depName);
            LOG.info("Department with {} name found", depName);
            return new ResponseEntity<>(companyDao.getDepartments(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new CustomErrorType("Unable to delete. Department with name " + depName + " is not there."),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> deleteEmployee(int id) {
        LOG.info("Fetching and Deleting employee with id {}", id);

        if (companyDao.deleteEmployee(id) > 0) {
            LOG.info("Employee with {} id found", id);
            return new ResponseEntity<>(companyDao.getEmployees(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomErrorType("Unable to delete employee with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> addDepartment(Department department, UriComponentsBuilder ucBuilder) {
        LOG.info("Creating Department : {}", department);

        if (companyDao.verifyDepartmentExistence(department.getDepName()) != null) {
            LOG.debug("Unable to create. A department with name {} already exist", department.getDepName());
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "Unable to create. A department with name " + department.getDepName() + " already exist."),
                    HttpStatus.CONFLICT);
        }
        LOG.debug("The department is: {}", department);
        companyDao.addDepartment(department);
        companyEventDao.addDepartmentToMongoDB(department);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/company/department").buildAndExpand(department.getDepId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> updateEmployeeJobTitle(int id, Employee employee) {
        Employee currentEmployee = companyDao.verifyEmployeeExistence(id);

        if (companyDao.verifyEmployeeExistence(id)== null) {
            LOG.debug("Unable to update. Employee with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Unable to update. Employee with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        LOG.info("Updating Employee with id {}", id);
        currentEmployee.setJobTitle(employee.getJobTitle());

        companyDao.changeEmployeeJobTitle(employee.getName(), employee.getlName(), currentEmployee.getJobTitle());
        return new ResponseEntity<>(currentEmployee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> addEmployee(Employee employee, UriComponentsBuilder ucBuilder) {

        if (companyDao.verifyEmployeeExistence(employee.getId()) != null) {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "Unable to create. An employee with id " + employee.getId() + " already exist."),
                    HttpStatus.CONFLICT);
        }
        LOG.debug("The employee is: {}", employee);
        employeeValidator.validate(employee);
        companyDao.addEmployee(employee);
        LOG.info("Adding employee with id {}", employee.getId());
        companyEventDao.addEmployeeToMongoDB(employee);
        companyMessagePublisher.publish(employee);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/company/employee").buildAndExpand(employee.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public void changeAnEmployeeDepartment(String name, String lName, String departmentName) {
        List<Employee> listEmp = companyDao.changeAnEmployeeDepartment(name, lName, departmentName);

        if (CollectionUtils.isEmpty(listEmp)) {
            companyDao.changeAnEmployeeDepartmentAndCheckIfManager(name, lName, departmentName);
            LOG.debug("Employee data has been updated successfully!");
            new ResponseEntity<>(HttpStatus.OK);
        } else {
            new ResponseEntity<>(
                    new CustomErrorType(
                            "The current employee is a manager and cannot be transferred"),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> getAnEmployeeById(int id) {
        Employee anEmployee = companyDao.getAnEmployeeById(id);
        if (null != anEmployee) {
            LOG.debug("Retrieved: [{}]", anEmployee);
            return new ResponseEntity<>(anEmployee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "The employee cannot be found"),
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Object> updateAnEmployee(int id, Employee employee) {
        if (null != employee) {
            companyDao.updateAnEmployee(id, employee);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "The employee cannot be found"),
                    HttpStatus.NOT_FOUND);
        }
    }
}

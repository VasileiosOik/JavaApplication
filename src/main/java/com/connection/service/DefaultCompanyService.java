package com.connection.service;

import com.connection.customerror.CustomErrorType;
import com.connection.dao.CompanyDao;
import com.connection.dao.CompanyMongoDao;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class DefaultCompanyService implements CompanyService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCompanyService.class);

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private CompanyMongoDao companyMongoDao;


    @Override
    public ResponseEntity<List<Employee>> returnAllEmployees() {
        List<Employee> employees = companyDao.getAllEmployees();

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Department>> returnAllDepartments() {
        List<Department> departments = companyDao.getAllDepartments();

        if (CollectionUtils.isEmpty(departments)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> getEmployeesInASpecificDepartment(String depName) {
        LOG.info("Fetching Employees that work in the {} department", depName);
        List<Employee> employees = companyDao.getAllEmployeesInADepartment(depName);

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
        List<Employee> employees = companyDao.getEmployeesByNumOfYearsWorked(number);

        if (CollectionUtils.isEmpty(employees)) {
            return new ResponseEntity<>(new CustomErrorType("Employees with " + number + "of years not found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> deleteDepartment(String depName) {
        LOG.info("Fetching and Deleting Department with name {}", depName);

        List<Department> departments = companyDao.getAllDepartments();

        if (!CollectionUtils.isEmpty(departments)) {
            for (Department dep : departments) {
                if (dep.getDepName().equals(depName)) {
                    LOG.info("Department with {} name found", depName);
                    companyDao.removeDepartment(depName);
                    return new ResponseEntity<>(companyDao.getAllDepartments(), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(
                new CustomErrorType("Unable to delete. Department with name " + depName + " is not there."),
                HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Object> deleteEmployee(int id) {
        LOG.info("Fetching and Deleting employee with id {}", id);

        List<Employee> employees = companyDao.getAllEmployees();

        if (!CollectionUtils.isEmpty(employees)) {

            for (Employee emp : employees) {
                if (emp.getId() == (id)) {
                    LOG.info("Employee with {} id found", id);
                    companyDao.removeEmployee(emp.getName(), emp.getLname());
                    return new ResponseEntity<>(companyDao.getAllDepartments(), HttpStatus.OK);
                }
            }
        }
        LOG.error("Unable to delete. Employee with id {} not found.", id);
        return new ResponseEntity<>(new CustomErrorType("Unable to delete. Employee with id " + id + " not found."),
                HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Object> addNewDepartment(Department department, UriComponentsBuilder ucBuilder) {
        LOG.info("Creating Department : {}", department);

        if (companyDao.verifyDepartmentExistence(department.getDepName()) == 0) {
            LOG.debug("Unable to create. A department with name {} already exist", department.getDepName());
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "Unable to create. A department with name " + department.getDepName() + " already exist."),
                    HttpStatus.CONFLICT);
        }

        companyDao.addDepartment(department);
        companyMongoDao.addDepartmentToMongoDB(department);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/company/department").buildAndExpand(department.getDepId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> updateEmployeeJobTitle(int id, Employee employee) {
        Employee currentEmployee = companyDao.verifyEmployeeExistence(id);

        if (currentEmployee == null) {
            LOG.debug("Unable to update. Employee with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Unable to update. Employee with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        LOG.info("Updating Employee with id {}", id);
        currentEmployee.setJobTitle(employee.getJobTitle());

        companyDao.changeEmployeeJobTitle(employee.getName(), employee.getLname(), currentEmployee.getJobTitle());
        return new ResponseEntity<>(currentEmployee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Object> addNewEmployee(Employee employee, UriComponentsBuilder ucBuilder) {

        if (companyDao.verifyEmployeeExistence(employee.getId()) != null) {
            return new ResponseEntity<>(
                    new CustomErrorType(
                            "Unable to create. An employee with name " + employee.getName() + " already exist."),
                    HttpStatus.CONFLICT);
        }

        companyDao.addEmployee(employee);
        companyMongoDao.addEmployeeToMongoDB(employee);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/company/employee").buildAndExpand(employee.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }
}

package com.connection.service;

import com.connection.customexception.AlreadyCreatedException;
import com.connection.customexception.NotFoundException;
import com.connection.dao.CompanyDao;
import com.connection.dao.CompanyEventDao;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.publisher.CompanyMessagePublisher;
import com.connection.validation.EmployeeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

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
    public List<Employee> getEmployees() {
        List<Employee> employees = companyDao.getEmployees();

        if (CollectionUtils.isEmpty(employees)) {
            throw new NotFoundException("Employees");
        }
        return employees;
    }

    @Override
    public List<Department> getDepartments() {
        List<Department> departments = companyDao.getDepartments();

        if (CollectionUtils.isEmpty(departments)) {
            throw new NotFoundException("Dpartments");
        }
        return departments;
    }

    @Override
    public List<Employee> getEmployeesInASpecificDepartment(String depName) {
        LOG.info("Fetching Employees that work in the {} department", depName);
        List<Employee> employees = companyDao.getEmployeesInASpecificDepartment(depName);

        if (CollectionUtils.isEmpty(employees)) {
            throw new NotFoundException("Employees in Specific Department not found");
        }
        return employees;
    }

    @Override
    public List<Employee> getEmployeesByNumOfYearsWorked(int number) {
        LOG.info("Fetching Employees with {} years of employment ", number);
        List<Employee> employees = companyDao.getEmployeesByNumOfYearsWorked(number);

        if (CollectionUtils.isEmpty(employees)) {
            throw new NotFoundException("Employees that worked years");
        }
        return employees;
    }

    @Override
    public List<Department> deleteDepartment(String depName) {
        LOG.info("Fetching and Deleting Department with name {}", depName);

        if (companyDao.verifyDepartmentExistence(depName) != null) {
            // because of the foreign keys constrains first you remove the
            // department and second you remove the employee
            companyDao.updateEmployeeDepartmentId(depName);
            companyDao.deleteDepartment(depName);
            LOG.info("Department with {} name found", depName);
            return companyDao.getDepartments();
        } else {
            throw new NotFoundException("Department Not Found");
        }
    }

    @Override
    public List<Employee> deleteEmployee(int id) {
        LOG.info("Fetching and Deleting employee with id {}", id);

        if (companyDao.deleteEmployee(id) > 0) {
            LOG.info("Employee with {} id found", id);
            return companyDao.getEmployees();
        } else {
            throw new NotFoundException("Employee to Delete Not Found");
        }
    }

    @Override
    public Department addDepartment(Department department, UriComponentsBuilder ucBuilder) {
        LOG.info("Creating Department : {}", department);

        if (companyDao.verifyDepartmentExistence(department.getDepName()) != null) {
            LOG.debug("Unable to create. A department with name {} already exist", department.getDepName());
            throw new AlreadyCreatedException(department.getDepName());
        }
        LOG.debug("The department is: {}", department);
        companyDao.addDepartment(department);
        companyEventDao.addDepartmentToMongoDB(department);
        return department;
    }

    @Override
    public Employee updateEmployeeJobTitle(int id, Employee employee) {
        Employee currentEmployee = companyDao.verifyEmployeeExistence(id);

        if (companyDao.verifyEmployeeExistence(id) == null) {
            LOG.debug("Unable to update. Employee with id {} not found.", id);
            throw new NotFoundException("Employee Not Found");
        }
        LOG.info("Updating Employee with id {}", id);
        currentEmployee.setJobTitle(employee.getJobTitle());

        companyDao.changeEmployeeJobTitle(employee.getName(), employee.getlName(), currentEmployee.getJobTitle());
        return currentEmployee;
    }

    @Override
    public Employee addEmployee(Employee employee, UriComponentsBuilder ucBuilder) {

        if (companyDao.verifyEmployeeExistence(employee.getId()) != null) {
            throw new AlreadyCreatedException(String.valueOf(employee.getId()));
        }
        LOG.debug("The employee is: {}", employee);
        employeeValidator.validate(employee);
        companyDao.addEmployee(employee);
        LOG.info("Adding employee with id {}", employee.getId());
        companyEventDao.addEmployeeToMongoDB(employee);
        companyMessagePublisher.publish(employee);
        return employee;
    }

    @Override
    public void changeAnEmployeeDepartment(String name, String lName, String departmentName) {
        List<Employee> listEmp = companyDao.changeAnEmployeeDepartment(name, lName, departmentName);

        if (!CollectionUtils.isEmpty(listEmp)) {
            companyDao.changeAnEmployeeDepartmentAndCheckIfManager(name, lName, departmentName);
            LOG.debug("Employee data has been updated successfully!");
        } else {
            throw new NotFoundException("The current employee is a manager and cannot be transferred");
        }
    }

    @Override
    public Employee getAnEmployeeById(int id) {
        Employee anEmployee = companyDao.getAnEmployeeById(id);
        if (null != anEmployee) {
            LOG.debug("Retrieved: [{}]", anEmployee);
            return anEmployee;
        } else {
            throw new NotFoundException("The employee cannot be found");
        }
    }

    @Override
    public Employee updateAnEmployee(int id, Employee employee) {
        if (null != employee) {
            companyDao.updateAnEmployee(id, employee);
            return employee;
        } else {
            throw new NotFoundException("Employee Not Found");
        }
    }
}

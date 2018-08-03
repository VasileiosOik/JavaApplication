package com.connection.dao;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.CompanyMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class CompanyDao {

    private final Logger log = LoggerFactory.getLogger(CompanyDao.class);

    @Autowired
    private CompanyMapper companyMapper;

    public List<Employee> getEmployeesByNumOfYearsWorked(int numOfYears) {

        List<Employee> listEmp = companyMapper.getEmployeesByNumOfYearsWorked(numOfYears);

        if (CollectionUtils.isEmpty(listEmp)) {
            log.debug("No employees found");
            return Collections.emptyList();
        }
        return listEmp;
    }

    public void removeEmployee(String name, String lName) {

        List<Employee> listEmp = companyMapper.showAllEmployees();

        if (!CollectionUtils.isEmpty(listEmp)) {
            listEmp.forEach(employee -> {
                if (employee.getName().equals(name) && employee.getlName().equals(lName)) {
                    companyMapper.removeEmployee(name, lName);
                    log.debug("Employee removed successfully");
                    removeEmployeeThatIsManager(listEmp, employee);
                }
            });
        } else {
            log.debug("There are no employees at all");
        }
    }

    private void removeEmployeeThatIsManager(List<Employee> listEmp, Employee employee) {
        for (Employee emp : listEmp) {
            if (emp.getManagerId() == employee.getId()) {
                companyMapper.removeEmployeeThatIsManager();
                log.debug("Employee was a manager so the Employees data are updated accordingly");
                break;
            }
        }
    }

    public List<Employee> getAllEmployees() {

        List<Employee> listEmp = companyMapper.showAllEmployees();

        if (CollectionUtils.isEmpty(listEmp)) {
            log.debug("No employees found");
            return Collections.emptyList();
        }

        return listEmp;
    }

    public List<Department> getAllDepartments() {
        List<Department> departments = companyMapper.showAllDepartments();

        if (CollectionUtils.isEmpty(departments)) {
            log.debug("No departments found");
            return Collections.emptyList();
        }
        return departments;
    }

    public List<Employee> getAllEmployeesInADepartment(String name) {

        List<Employee> employees = companyMapper.employeesInSpecificDepartment(name);

        if (CollectionUtils.isEmpty(employees)) {
            log.debug("No employees found in this department [{}]", name);
            return Collections.emptyList();
        }
        return employees;
    }

    public List<Employee> getAllEmployeesByTheirManagers(String name, String lastName) {

        List<Employee> employees = companyMapper.getAllEmployeesByTheirManager(name, lastName);
        if (!CollectionUtils.isEmpty(employees)) {
            log.info("Displaying Employees in it: {}%n", employees);
            return employees;
        } else {
            log.debug("No Manager found with such a name");
            return Collections.emptyList();
        }
    }

    public void changeEmployeeJobTitle(String name, String lName, String jobTitle) {

        List<Employee> results = companyMapper.showAllEmployees();
        if (!CollectionUtils.isEmpty(results)) {
            for (Employee employee : results) {
                if (employee.getName().equals(name) && employee.getlName().equals(lName)) {
                    companyMapper.changeEmployeeJobTitle(name, lName, jobTitle);
                    log.debug("Employee's job title has been updated successfully!");
                }
            }
        } else {
            log.debug("No Employee found at all");
        }

    }

    public void addDepartment(Department department) {
        if (verifyDepartmentExistence(department.getDepName()) == 0) {
            log.debug("Cannot add department with the same name");
        } else {
            companyMapper.addDepartment(department);
            log.debug("Department was saved successfully!");
        }
    }

    public void changeAnEmployeeDepartment(String name, String lName, String departmentName) {

        List<Employee> listEmp = companyMapper.changeAnEmployeeDepartment(name, lName, departmentName);

        if (CollectionUtils.isEmpty(listEmp)) {
            companyMapper.changeAnEmployeeDepartmentAndCheckIfManager(name, lName, departmentName);
            log.debug("Employee data has been updated successfully!");
        } else {
            log.debug("The current employee is a manager and cannot be transferred");
        }
    }

    public void addEmployee(Employee employee) {
        if (addEmployeeEnchanced(employee.getManagerId(), employee.getDepartmentId())) {
            employee.setHireDate(determineTheHireDate(employee.getHireDate()));
            companyMapper.addEmployee(employee);
            log.debug("Employee was saved successfully!");
        }
    }

    private boolean addEmployeeEnchanced(int managerId, int departmentId) {
        if (verifyDepartmentExistence(companyMapper.departmentFound(departmentId)) == 0
                && (companyMapper.managerOfADepartment(departmentId)) == managerId) {
            log.debug("Both exists under the same section in the system.");
            return true;
        } else {
            log.debug("They are not in the same section in the system. Therefore an employee cannot be added. Please try again");
            return false;
        }
    }

    private String determineTheHireDate(String hireDate) {

        boolean dateStory;
        do {
            LocalDate date1 = LocalDate.parse(hireDate);
            // 1st check
            dateStory = date1.isBefore(LocalDate.now());
            // 2nd check
            LocalDate currentDatePlusOneMonth = LocalDate.now().plusDays(30);

            if (date1.isBefore(currentDatePlusOneMonth)) {
                dateStory = true;
            } else {
                log.debug("The date is more than one month in front");
            }
        } while (!dateStory);

        return hireDate;
    }

    public int verifyDepartmentExistence(String name) {
        List<Department> departments = companyMapper.showAllDepartments();
        if (!CollectionUtils.isEmpty(departments)) {
            for (Department department : departments) {
                if (department.getDepName().equals(name)) {
                    log.debug("Department exists.");
                    return 0;
                }
            }
        }
        log.debug("Department does not exist");
        return 1;
    }

    public Employee verifyEmployeeExistence(Integer id) {
        return companyMapper.verifyIfEmployeeExists(id);

    }

    public void removeDepartment(String name) {

        if (verifyDepartmentExistence(name) == 0) {
            // because of the foreign keys constrains first you remove the
            // department and second you remove the employee
            companyMapper.updateEmployee(name);
            companyMapper.removeDepartment(name);
            log.info("Department removed successfully");
        } else {
            log.info("Department does not exist");
        }
    }
}

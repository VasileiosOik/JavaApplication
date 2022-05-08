package com.connection.mapper;

import com.connection.application.Application;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest(classes = Application.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
public class CompanyMapperIT {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyMapperIT.class);

    @Autowired
    private CompanyMapper companyMapper;

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testShowAllEmployees() {
        List<Employee> employees = companyMapper.getAllEmployees();
        LOG.debug("The employees are: {}", employees);
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void getAnEmployee() {
        Employee employee = companyMapper.getAnEmployee(100006);
        assertNotNull(employee);
        assertEquals("Petra", employee.getName());
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testShowAllDepartments() {
        List<Department> departments = companyMapper.getAllDepartments();
        LOG.debug("The departments are: {}", departments);
        assertNotNull(departments);
        assertFalse(departments.isEmpty());
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testEmployeesInSpecificDepartment() {
        List<Employee> employees = companyMapper.getEmployeesInSpecificDepartment("Technology");
        LOG.debug("The employees in the specific department are: {}", employees);
        assertEquals(3, employees.size());
        assertFalse(employees.isEmpty());
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testGetAllEmployeesByTheirManager() {
        List<Employee> employees = companyMapper.getAllEmployeesByTheirManager("Kevin", "Withers");

        List<Employee> emp = new ArrayList<>(employees);
        LOG.info("Employees by their manager list: {}", emp);
        assertNotNull(employees);
        assertEquals(2, employees.size());
        assertFalse(employees.isEmpty());
        assertEquals("Nigel", employees.get(0).getName());
        assertEquals("Pentland", employees.get(0).getlName());
        assertEquals("Petra", employees.get(1).getName());
        assertEquals("Moody", employees.get(1).getlName());
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testGetEmployeesByNumOfYearsWorked() {
        List<Employee> employees = companyMapper.getEmployeesByNumOfYearsWorked(20);
        LOG.debug("The employees are: {}", employees);
        assertNotNull(employees);
        assertEquals(5, employees.size());
        assertEquals("David", employees.get(0).getName());
        assertEquals("Kevin", employees.get(1).getName());
        assertEquals("Tracey", employees.get(2).getName());

    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/ChangeEmployeeTitle.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testChangeEmployeeJobTitle() {
        companyMapper.changeEmployeeJobTitle("John", "Smith", "Developer");
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/RemovedEmployeesExpected.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testRemoveEmployee() {
        int removeEmployee = companyMapper.removeEmployee(100008);
        assertEquals(1, removeEmployee);
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testRemoveEmployeeThatDoesNotExist() {
        int removeEmployee = companyMapper.removeEmployee(1);
        assertEquals(0, removeEmployee);
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/departmentTestData/DepartmentsRemainedExpected.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testRemoveDepartment() {
        companyMapper.updateEmployeeDataBeforeDeleteOfDepartment("Sales");
        companyMapper.removeDepartment("Sales");
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/updateAnEmployee.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void updateAnEmployee() {
        Employee employee = new EmployeeBuilder().withJobTitle("Senior Analyst")
                .withHireDate(LocalDate.of(2014, Month.JANUARY, 25))
                .withName("Nigel").withDepartmentId(1001)
                .withManageId(100002).withLname("Pentland").build();
        companyMapper.updateAnEmployee(100007, employee);
    }

    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/employeeTestData/EmployeeAfterAdding.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testAddEmployee() {
        companyMapper.addEmployee(getEmployee());
        List<Employee> employees = companyMapper.getAllEmployees();
        assertEquals(15, employees.size());
    }


    @Test
    @DatabaseSetup("/employeeTestData/EmployeesAndDepartmentsFilled.xml")
    @ExpectedDatabase(assertionMode= DatabaseAssertionMode.NON_STRICT, value = "/departmentTestData/DepartmentAfterAdding.xml")
    @DatabaseTearDown("/departmentTestData/ClearData.xml")
    public void testAddDepartment() {
        companyMapper.addDepartment(getDepartment());
    }

    private Employee getEmployee() {
        Employee emp = new Employee();
        emp.setName("Alex");
        emp.setlName("dean");
        emp.setId((companyMapper.getMaxEmployeeId() + 1));
        emp.setJobTitle("Tester");
        emp.setDepartmentId(1003);
        emp.setManagerId(100004);
        emp.setHireDate(LocalDate.of(2016, Month.MAY, 29));
        return emp;
    }

    private Department getDepartment() {
        Department department = new Department();
        department.setDepId((companyMapper.getMaxDepartmentId() + 1));
        department.setDepName("Finance");
        return department;
    }
}

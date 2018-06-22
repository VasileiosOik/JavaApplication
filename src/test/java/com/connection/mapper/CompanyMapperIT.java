package com.connection.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.connection.application.Application;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
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

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest(classes = Application.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
public class CompanyMapperIT {

	private static final Logger LOG = LoggerFactory.getLogger(CompanyMapperIT.class);

	@Autowired
	private CompanyMapper companyMapper;

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testShowAllEmployees() {
		List<Employee> employees = companyMapper.showAllEmployees();
		LOG.debug("The employees are: {}", employees);
		assertNotNull(employees);
		assertTrue(!employees.isEmpty());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testShowAllDepartments() {
		List<Department> departments = companyMapper.showAllDepartments();
		LOG.debug("The departments are: {}", departments);
		assertNotNull(departments);
		assertTrue(!departments.isEmpty());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testEmployeesInSpecificDepartment() {
		List<Employee> employees = companyMapper.employeesInSpecificDepartment("Technology");
		LOG.debug("The employees in the specific department are: {}", employees);
		assertEquals(3, employees.size());
		assertTrue(!employees.isEmpty());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetAllEmployeesByTheirManager() {
		List<Employee> employees = companyMapper.getAllEmployeesByTheirManager("Kevin", "Withers");

		List<Employee> emp = new ArrayList<>(employees);
		LOG.info("Employees by their manager list: {}", emp);
		assertNotNull(employees);
		assertEquals(2, employees.size());
		assertTrue(!employees.isEmpty());
		assertEquals("Nigel", employees.get(0).getName());
		assertEquals("Pentland", employees.get(0).getLname());
		assertEquals("Petra", employees.get(1).getName());
		assertEquals("Moody", employees.get(1).getLname());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testGetEmployeesByNumOfYearsWorked() {
		List<Employee> employees = companyMapper.getEmployeesByNumOfYearsWorked(20);
		LOG.debug("The employees are: {}", employees);
		assertNotNull(employees);
		assertEquals(2, employees.size());
		assertEquals("Kevin", employees.get(0).getName());
		assertEquals("Withers", employees.get(0).getLname());
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/ChangeEmployeeTitle.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testChangeEmployeeJobTitle() {
		companyMapper.changeEmployeeJobTitle("John", "Smith", "Developer");
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/RemovedEmployeesExpected.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testRemoveEmployee() {
		companyMapper.removeEmployee("Patricia", "Murray");
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/DepartmentsRemainedExpected.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testRemoveDepartment() {
		companyMapper.updateEmployeeDataBeforeDeleteOfDepartment("Sales");
		companyMapper.removeDepartment("Sales");
	}

	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/EmployeeAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddEmployee() {
		companyMapper.addEmployee(getEmployee());
		List<Employee> employees = companyMapper.showAllEmployees();
		assertEquals(15, employees.size());
	}


	@Test
	@DatabaseSetup("/EmployeesAndDepartmentsFilled.xml")
	@ExpectedDatabase("/DepartmentAfterAdding.xml")
	@DatabaseTearDown("/ClearData.xml")
	public void testAddDepartment() {
		companyMapper.addDepartment(getDepartment());
	}

	private Employee getEmployee() {
		Employee emp = new Employee();
		emp.setName("Alex");
		emp.setLname("Tso");
		emp.setId((companyMapper.getMaxEmployeeId() + 1));
		emp.setJobTitle("Tester");
		emp.setDepartmentId(1003);
		emp.setManagerId(100004);
		emp.setHiredate("2016-05-29");
		return emp;
	}

	private Department getDepartment() {
		Department department = new Department();
		department.setDepId((companyMapper.getMaxDepartmentId() + 1));
		department.setDepName("Finance");
		return department;
	}
}
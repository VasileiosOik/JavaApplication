package com.connection.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.connection.mapper.EmployeeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.CompanyMapper;

@RunWith(MockitoJUnitRunner.class)
public class CompanyDaoTest {

    @Mock
	private CompanyMapper companyMapper;

	@InjectMocks
	private CompanyDao companyDao;

	@Captor
	private ArgumentCaptor<String> captor1;
	
	@Captor
	private ArgumentCaptor<String> captor2;

	@Captor
	private ArgumentCaptor<Employee> empCaptor;

	@Captor
	private ArgumentCaptor<Department> depCaptor;

	private static final Logger LOG = LoggerFactory.getLogger(CompanyDaoTest.class);

	private static final String SALES = "Sales";

    private static final String TECHNOLOGY = "Technology";

	@Test
	public void returnAllEmployees() {

		when(companyMapper.showAllEmployees()).thenReturn(getMockEmployee());
		LOG.debug("The returned employee is [ {} ]", getMockEmployee());

		companyDao.getAllEmployees();

		Mockito.verify(companyMapper, times(1)).showAllEmployees();
	}
	
	@Test
	public void returnAllDepartments() {

		when(companyMapper.showAllDepartments()).thenReturn(getMockDepartments());
		LOG.debug("The returned department is [ {} ]", getMockDepartments());

		companyDao.getAllDepartments();

		Mockito.verify(companyMapper, times(1)).showAllDepartments();
	}

	@Test
	public void testEmployeesInSpecificDepartment() {
		when(companyMapper.employeesInSpecificDepartment(SALES)).thenReturn(getMockEmployee());
		companyDao.getAllEmployeesInADepartment(SALES);

		Mockito.verify(companyMapper, times(1)).employeesInSpecificDepartment(captor1.capture());
		LOG.debug("The value is [ {} ]", captor1.getValue());
		assertEquals(SALES, captor1.getValue());

	}
	
	@Test
	public void testEmployeesbyManager() {
		when(companyMapper.getAllEmployeesByTheirManager("Bill", "Eco")).thenReturn(getMockEmployee());
		companyDao.getAllEmployeesByTheirManagers("Bill", "Eco");

		Mockito.verify(companyMapper, times(1)).getAllEmployeesByTheirManager(captor1.capture(), captor2.capture());
		assertEquals("Bill", captor1.getValue());
		assertEquals("Eco", captor2.getValue());

	}

	@Test
	public void testAddDepartment() {
		Mockito.doNothing().when(companyMapper).addDepartment(getMockDepartment());
		companyDao.addDepartment(getMockDepartment());

		Mockito.verify(companyMapper, times(1)).addDepartment(depCaptor.capture());

		assertEquals(TECHNOLOGY, depCaptor.getValue().getDepName());
		assertEquals(1003, depCaptor.getValue().getDepId());
	}

	@Test
	public void testAddEmployee() {

		when(companyMapper.departmentFound(1003)).thenReturn(TECHNOLOGY);

		when(companyMapper.managerOfADepartment(1003)).thenReturn(100004);

		when(companyMapper.showAllDepartments()).thenReturn(getMockDepartments());

		Mockito.doNothing().when(companyMapper).addEmployee(getOneMockEmployee());

		companyDao.addEmployee(getOneMockEmployee());

		Mockito.verify(companyMapper, times(1)).departmentFound(1003);

		Mockito.verify(companyMapper, times(1)).managerOfADepartment(1003);

		Mockito.verify(companyMapper, times(1)).showAllDepartments();

		Mockito.verify(companyMapper, times(1)).addEmployee(empCaptor.capture());

		assertEquals(100015, empCaptor.getValue().getId());
		assertEquals("Alex", empCaptor.getValue().getName());
		assertEquals("Tso", empCaptor.getValue().getlName());
		assertEquals("Tester", empCaptor.getValue().getJobTitle());

	}

	@Test
	public void testChangeAnEmployeeDepartment()
	{
		when(companyMapper.changeAnEmployeeDepartment(anyString(), anyString(), anyString()))
				.thenReturn(getMockEmployee());
		
		when(companyMapper.changeAnEmployeeDepartmentAndCheckIfManager(anyString(), anyString(), anyString()))
				.thenReturn(getOneMockEmployee());


		companyDao.changeAnEmployeeDepartment(anyString(), anyString(), anyString());

		Mockito.verify(companyMapper, times(1)).changeAnEmployeeDepartment(anyString(), anyString(), anyString());

		Mockito.verify(companyMapper, times(0)).changeAnEmployeeDepartmentAndCheckIfManager(anyString(), anyString(),
				anyString());

	}

	private List<Employee> getMockEmployee() {
		Employee employee = new Employee();
		employee.setName("Bill");
		employee.setlName("Eco");
		employee.setJobTitle("Developer");
		employee.setId(1);
		employee.setManagerId(100015);
		employee.setDepartmentId(1001);
		employee.setHireDate(LocalDate.of(1988, Month.OCTOBER, 23));

		List<Employee> emp = new ArrayList<>();
		emp.add(employee);

		return emp;
	}


	private Employee getOneMockEmployee() {
		EmployeeBuilder employeeBuilder = new EmployeeBuilder()
                .withDepartmentId(1003)
                .withHireDate(LocalDate.of(2016, Month.MAY, 29 ))
                .withId(100015)
                .withJobTitle("Tester")
                .withName("Alex")
                .withLname("Tso")
                .withManageId(100004);
		return employeeBuilder.build();
	}


	private Department getMockDepartment() {
		Department department = new Department();
		department.setDepId(1003);
		department.setDepName(TECHNOLOGY);

		return department;

	}
	
	private List<Department> getMockDepartments() {
		Department department = new Department();
		department.setDepId(1003);
		department.setDepName(TECHNOLOGY);
		
		List<Department> dep = new ArrayList<>();
		dep.add(department);

		return dep;

	}
}

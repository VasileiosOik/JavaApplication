package com.connection.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.ConnectionMapper;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
public class ConnectionDAOTest {



    @Mock
	private ConnectionMapper connectionMapper;

	@InjectMocks
	private ConnectionDAO connectionDAO;

	@Captor
	private ArgumentCaptor<String> captor1;
	
	@Captor
	private ArgumentCaptor<String> captor2;

	@Captor
	private ArgumentCaptor<Employee> empCaptor;

	@Captor
	private ArgumentCaptor<Department> depCaptor;

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionDAOTest.class);

	private static final String SALES = "Sales";

    private static final String TECHNOLOGY = "Technology";

	@Test
	public void returnAllEmployees() {

		when(connectionMapper.showAllEmployees()).thenReturn(getMockEmployee());
		LOG.debug("The returned employee is [ {} ]", getMockEmployee());

		connectionDAO.getAllEmployees();

		Mockito.verify(connectionMapper, times(1)).showAllEmployees();
	}
	
	@Test
	public void returnAllDepartments() {

		when(connectionMapper.showAllDepartments()).thenReturn(getMockDepartments());
		LOG.debug("The returned department is [ {} ]", getMockDepartments());

		connectionDAO.getAllDepartments();

		Mockito.verify(connectionMapper, times(1)).showAllDepartments();
	}

	@Test
	public void testEmployeesInSpecificDepartment() {
		when(connectionMapper.employeesInSpecificDepartment(SALES)).thenReturn(getMockEmployee());
		connectionDAO.getAllEmployeesInADepartment(SALES);

		Mockito.verify(connectionMapper, times(1)).employeesInSpecificDepartment(captor1.capture());
		LOG.debug("The value is [ {} ]", captor1.getValue());
		assertEquals(SALES, captor1.getValue());

	}
	
	@Test
	public void testEmployeesbyManager() {
		when(connectionMapper.getAllEmployeesByTheirManager("Bill", "Eco")).thenReturn(getMockEmployee());
		connectionDAO.getAllEmployeesByTheirManagers("Bill", "Eco");

		Mockito.verify(connectionMapper, times(1)).getAllEmployeesByTheirManager(captor1.capture(), captor2.capture());
		assertEquals("Bill", captor1.getValue());
		assertEquals("Eco", captor2.getValue());

	}

	@Test
	public void testAddDepartment() {
		Mockito.doNothing().when(connectionMapper).addDepartment(getMockDepartment());
		connectionDAO.addDepartment(getMockDepartment());

		Mockito.verify(connectionMapper, times(1)).addDepartment(depCaptor.capture());

		assertEquals(TECHNOLOGY, depCaptor.getValue().getDepName());
		assertEquals(1003, depCaptor.getValue().getDepId());
	}

	@Test
	public void testAddEmployee() {

		when(connectionMapper.departmentFound(1003)).thenReturn(TECHNOLOGY);

		when(connectionMapper.managerOfADepartment(1003)).thenReturn(100004);

		when(connectionMapper.showAllDepartments()).thenReturn(getMockDepartments());

		Mockito.doNothing().when(connectionMapper).addEmployee(getOneMockEmployee());

		connectionDAO.addEmployee(getOneMockEmployee());

		Mockito.verify(connectionMapper, times(1)).departmentFound(1003);

		Mockito.verify(connectionMapper, times(1)).managerOfADepartment(1003);

		Mockito.verify(connectionMapper, times(1)).showAllDepartments();

		Mockito.verify(connectionMapper, times(1)).addEmployee(empCaptor.capture());

		assertEquals(100015, empCaptor.getValue().getId());
		assertEquals("Alex", empCaptor.getValue().getName());
		assertEquals("Tso", empCaptor.getValue().getLname());
		assertEquals("Tester", empCaptor.getValue().getJobTitle());

	}

	@Test
	public void testChangeAnEmployeeDepartment()
	{
		when(connectionMapper.changeAnEmployeeDepartment(anyString(), anyString(), anyString()))
				.thenReturn(getMockEmployee());
		
		when(connectionMapper.changeAnEmployeeDepartmentAndCheckIfManager(anyString(), anyString(), anyString()))
				.thenReturn(getOneMockEmployee());


		connectionDAO.changeAnEmployeeDepartment(anyString(), anyString(), anyString());

		Mockito.verify(connectionMapper, times(1)).changeAnEmployeeDepartment(anyString(), anyString(), anyString());

		Mockito.verify(connectionMapper, times(0)).changeAnEmployeeDepartmentAndCheckIfManager(anyString(), anyString(),
				anyString());

	}

	private List<Employee> getMockEmployee() {
		Employee employee = new Employee();
		employee.setName("Bill");
		employee.setLname("Eco");
		employee.setJobTitle("Developer");
		employee.setId(1);
		employee.setManagerId(100015);
		employee.setDepartmentId(1001);
		employee.setHiredate("1988-10-23");

		List<Employee> emp = new ArrayList<>();
		emp.add(employee);

		return emp;
	}


	private Employee getOneMockEmployee() {
		Employee employee = new Employee();
		employee.setName("Alex");
		employee.setLname("Tso");
		employee.setJobTitle("Tester");
		employee.setId(100015);
		employee.setManagerId(100004);
		employee.setDepartmentId(1003);
		employee.setHiredate("2016-05-29");

		return employee;

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

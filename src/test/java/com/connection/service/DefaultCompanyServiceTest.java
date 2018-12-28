package com.connection.service;

import com.connection.dao.CompanyDao;
import com.connection.dao.CompanyEventDao;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.DepartmentBuilder;
import com.connection.mapper.EmployeeBuilder;
import com.connection.publisher.CompanyMessagePublisher;
import com.connection.validation.EmployeeValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCompanyServiceTest {

    @InjectMocks
    private DefaultCompanyService defaultCompanyService;

    @Mock
    private CompanyDao companyDao;

    @Mock
    private CompanyEventDao companyEventDao;

    @Mock
    private CompanyMessagePublisher companyMessagePublisher;

    @Mock
    private EmployeeValidator employeeValidator;

    private static final String SALES = "Sales";

    private static final String TECHNOLOGY = "Technology";

    @Test
    public void returnAllEmployees_WhenPopulated() {

        when(companyDao.getEmployees()).thenReturn(Collections.singletonList(new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build()));

        defaultCompanyService.returnAllEmployees();

        Mockito.verify(companyDao, times(1)).getEmployees();
    }

    @Test
    public void returnAllEmployeesNotPopulated_shouldResultNoDataReturned() {

        when(companyDao.getEmployees()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Employee>> listResponseEntity = defaultCompanyService.returnAllEmployees();
        assertEquals(HttpStatus.NOT_FOUND, listResponseEntity.getStatusCode());

        Mockito.verify(companyDao, times(1)).getEmployees();
    }


    @Test
    public void returnAllDepartments() {

        when(companyDao.getDepartments()).thenReturn(Collections.singletonList(new DepartmentBuilder()
                .withId(1005)
                .withName("Finance").build()));

        defaultCompanyService.returnAllDepartments();

        Mockito.verify(companyDao, times(1)).getDepartments();
    }

    @Test
    public void testEmployeesInSpecificDepartment() {
        when(companyDao.getEmployeesInASpecificDepartment(SALES)).thenReturn(Collections.singletonList(new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build()));

        ResponseEntity<Object> employeesInASpecificDepartment = defaultCompanyService.getEmployeesInASpecificDepartment(SALES);
        assertEquals(HttpStatus.OK, employeesInASpecificDepartment.getStatusCode());

        Mockito.verify(companyDao, times(1)).getEmployeesInASpecificDepartment(SALES);
    }

    @Test
    public void testAddDepartment() {
        Department department = new DepartmentBuilder().withId(1003).withName(TECHNOLOGY).build();

        Mockito.doNothing().when(companyDao).addDepartment(department);

        when(companyDao.verifyDepartmentExistence(TECHNOLOGY)).thenReturn(null);

        doNothing().when(companyEventDao).addDepartmentToMongoDB(department);

        defaultCompanyService.addNewDepartment(department, aComponentsBuilder());

        Mockito.verify(companyDao, times(1)).addDepartment(department);
        Mockito.verify(companyEventDao, times(1)).addDepartmentToMongoDB(department);
        Mockito.verify(companyDao, times(1)).verifyDepartmentExistence(department.getDepName());
    }

    private UriComponentsBuilder aComponentsBuilder() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        return builder.path("");
    }

    @Test
    public void testAddEmployee() {
        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer")
                .build();

        when(companyDao.verifyEmployeeExistence(1)).thenReturn(null);

        Mockito.doNothing().when(companyDao).addEmployee(employee);

        defaultCompanyService.addNewEmployee(employee, aComponentsBuilder());

        Mockito.verify(companyDao, times(1)).verifyEmployeeExistence(1);

        Mockito.verify(companyDao, times(1)).addEmployee(employee);

        Mockito.verify(companyMessagePublisher, times(1)).publish(employee);

        Mockito.verify(employeeValidator, times(1)).validate(employee);
    }

    @Test
    public void testChangeAnEmployeeDepartment_whenEmployeeIsManager() {
        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer")
                .build();

        when(companyDao.changeAnEmployeeDepartment(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(employee));


        defaultCompanyService.changeAnEmployeeDepartment(employee.getName(), employee.getlName(), "Research");

        Mockito.verify(companyDao, times(1)).changeAnEmployeeDepartment(anyString(), anyString(), anyString());

        Mockito.verify(companyDao, times(0)).changeAnEmployeeDepartmentAndCheckIfManager(anyString(), anyString(),
                anyString());

    }

    @Test
    public void testChangeAnEmployeeDepartment_whenEmployeeIsNotManager() {

        when(companyDao.changeAnEmployeeDepartment(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        defaultCompanyService.changeAnEmployeeDepartment(anyString(), anyString(), anyString());

        Mockito.verify(companyDao, times(1)).changeAnEmployeeDepartment(anyString(), anyString(), anyString());

        Mockito.verify(companyDao, times(1)).changeAnEmployeeDepartmentAndCheckIfManager(anyString(), anyString(),
                anyString());

    }


    @Test
    public void testGetAnEmployee() {

        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer")
                .build();

        when(companyDao.getAnEmployeeById(1)).thenReturn(employee);

        defaultCompanyService.getAnEmployee(1);

        verify(companyDao, times(1)).getAnEmployeeById(1);

    }

    @Test
    public void testGetAnEmployee_whenDoesNotExist() {


        when(companyDao.getAnEmployeeById(1)).thenReturn(null);

        ResponseEntity<Object> anEmployee = defaultCompanyService.getAnEmployee(1);
        assertEquals(HttpStatus.NOT_FOUND, anEmployee.getStatusCode());

        verify(companyDao, times(1)).getAnEmployeeById(1);
    }

    @Test
    public void testUpdateAnEmployee() {

        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer")
                .build();

        defaultCompanyService.updateAnEmployee(1, employee);

        verify(companyDao, times(1)).updateAnEmployee(1, employee);

    }

    @Test
    public void testUpdateAnEmployee_notFound() {


        ResponseEntity<Object> employee = defaultCompanyService.updateAnEmployee(1, null);
        assertEquals(HttpStatus.NOT_FOUND, employee.getStatusCode());

        verify(companyDao, times(0)).updateAnEmployee(1, null);

    }
}

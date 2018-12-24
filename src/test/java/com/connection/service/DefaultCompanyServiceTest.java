package com.connection.service;

import com.connection.dao.CompanyMongoDao;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.CompanyMapper;
import com.connection.mapper.DepartmentBuilder;
import com.connection.mapper.EmployeeBuilder;
import com.connection.publisher.ActionMessagePublisher;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCompanyServiceTest {

    @InjectMocks
    private DefaultCompanyService defaultCompanyService;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private CompanyMongoDao companyMongoDao;

    @Mock
    private ActionMessagePublisher actionMessagePublisher;

    @Mock
    private EmployeeValidator employeeValidator;

    private static final String SALES = "Sales";

    private static final String TECHNOLOGY = "Technology";

    @Test
    public void returnAllEmployees() {

        when(companyMapper.showAllEmployees()).thenReturn(Collections.singletonList(new EmployeeBuilder()
                .withName("Bill")
                .withName("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build()));

        defaultCompanyService.returnAllEmployees();

        Mockito.verify(companyMapper, times(1)).showAllEmployees();
    }

    @Test
    public void returnAllDepartments() {

        when(companyMapper.showAllDepartments()).thenReturn(Collections.singletonList(new DepartmentBuilder()
                .withId(1005)
                .withName("Finance").build()));

        defaultCompanyService.returnAllDepartments();

        Mockito.verify(companyMapper, times(1)).showAllDepartments();
    }

    @Test
    public void testEmployeesInSpecificDepartment() {
        when(companyMapper.employeesInSpecificDepartment(SALES)).thenReturn(Collections.singletonList(new EmployeeBuilder()
                .withName("Bill")
                .withName("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build()));

        ResponseEntity<Object> employeesInASpecificDepartment = defaultCompanyService.getEmployeesInASpecificDepartment(SALES);
        assertEquals(HttpStatus.OK, employeesInASpecificDepartment.getStatusCode());

        Mockito.verify(companyMapper, times(1)).employeesInSpecificDepartment(SALES);
    }

    @Test
    public void testAddDepartment() {
        Department department = new DepartmentBuilder().withId(1003).withName(TECHNOLOGY).build();

        Mockito.doNothing().when(companyMapper).addDepartment(department);

        when(companyMapper.verifyDepartmentExistence(TECHNOLOGY)).thenReturn(null);

        doNothing().when(companyMongoDao).addDepartmentToMongoDB(department);

        defaultCompanyService.addNewDepartment(department, aComponentsBuilder());

        Mockito.verify(companyMapper, times(1)).addDepartment(department);
        Mockito.verify(companyMongoDao, times(1)).addDepartmentToMongoDB(department);
        Mockito.verify(companyMapper, times(1)).verifyDepartmentExistence(department.getDepName());
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

        when(companyMapper.verifyIfEmployeeExists(1)).thenReturn(null);

        Mockito.doNothing().when(companyMapper).addEmployee(employee);

        defaultCompanyService.addNewEmployee(employee, aComponentsBuilder());

        Mockito.verify(companyMapper, times(1)).verifyIfEmployeeExists(1);

        Mockito.verify(companyMapper, times(1)).addEmployee(employee);

        Mockito.verify(actionMessagePublisher, times(1)).publish(employee);

        Mockito.verify(employeeValidator, times(1)).validate(employee);
    }

    @Test
    public void testChangeAnEmployeeDepartment() {
        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer")
                .build();

        when(companyMapper.changeAnEmployeeDepartment(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(employee));


        defaultCompanyService.changeAnEmployeeDepartment(employee.getName(), employee.getlName(), "Research");

        Mockito.verify(companyMapper, times(1)).changeAnEmployeeDepartment(anyString(), anyString(), anyString());

        Mockito.verify(companyMapper, times(0)).changeAnEmployeeDepartmentAndCheckIfManager(anyString(), anyString(),
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

        when(companyMapper.getAnEmployee(1)).thenReturn(employee);

        defaultCompanyService.getAnEmployee(1);

        verify(companyMapper, times(1)).getAnEmployee(1);

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

        verify(companyMapper, times(1)).updateAnEmployee(1, employee);

    }
}

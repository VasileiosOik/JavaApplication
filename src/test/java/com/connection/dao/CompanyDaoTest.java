package com.connection.dao;

import com.connection.domain.Employee;
import com.connection.mapper.CompanyMapper;
import com.connection.mapper.DepartmentBuilder;
import com.connection.mapper.EmployeeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyDaoTest {


    @InjectMocks
    private CompanyDao companyDao;

    @Mock
    private CompanyMapper companyMapper;

    private static final String SALES = "Sales";

    private static final int NUM_OF_YEARS = 10;

    @Test
    public void getAllEmployees() {
        when(companyMapper.getAllEmployees()).thenReturn(Collections.singletonList(new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build()));
        companyDao.getEmployees();
        verify(companyMapper, times(1)).getAllEmployees();
    }

    @Test
    public void getAllDepartments() {

        when(companyMapper.getAllDepartments()).thenReturn(Collections.singletonList(new DepartmentBuilder()
                .withId(1005)
                .withName("Finance").build()));

        companyDao.getDepartments();

        Mockito.verify(companyMapper, times(1)).getAllDepartments();
    }

    @Test
    public void testEmployeesInSpecificDepartment() {
        when(companyMapper.getEmployeesInSpecificDepartment(SALES)).thenReturn(Collections.singletonList(new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build()));

        List<Employee> employeesInASpecificDepartment = companyDao.getEmployeesInASpecificDepartment(SALES);
        assertEquals(1, employeesInASpecificDepartment.size());

        Mockito.verify(companyMapper, times(1)).getEmployeesInSpecificDepartment(SALES);
    }

    @Test
    public void getEmployeesByNumOfYearsWorked() {
        when(companyMapper.getEmployeesByNumOfYearsWorked(NUM_OF_YEARS)).thenReturn(Collections.singletonList(new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build()));

        List<Employee> employees = companyDao.getEmployeesByNumOfYearsWorked(NUM_OF_YEARS);
        assertEquals(1, employees.size());

        Mockito.verify(companyMapper, times(1)).getEmployeesByNumOfYearsWorked(NUM_OF_YEARS);
    }

    @Test
    public void deleteDepartment() {
        companyDao.deleteDepartment(SALES);
        verify(companyMapper, times(1)).removeDepartment(SALES);
    }

    @Test
    public void deleteEmployee() {
        companyDao.deleteEmployee(anyInt());
        verify(companyMapper, times(1)).removeEmployee(anyInt());
    }

}

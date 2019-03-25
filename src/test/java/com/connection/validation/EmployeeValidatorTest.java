package com.connection.validation;

import com.connection.customexception.IllegalArgumentValidatorException;
import com.connection.domain.Employee;
import com.connection.mapper.EmployeeBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeValidatorTest {

    @InjectMocks
    private EmployeeValidator employeeValidator;


    @Test
    public void validatorsAreCalledInOrder() {
        employeeValidator.setupValidators();

        Set<Validator> validators = employeeValidator.getValidators();

        assertEquals(2, validators.size());
    }


    @Test(expected = IllegalArgumentValidatorException.class)
    public void noValidName_resultsInException() {
        employeeValidator.setupValidators();

        Employee employee = new EmployeeBuilder()
                .withName("Bill12")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build();

        employeeValidator.validate(employee);

    }

    @Test(expected = IllegalArgumentValidatorException.class)
    public void noValidJobTitle_resultsInException() {
        employeeValidator.setupValidators();

        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Manager").build();

        employeeValidator.validate(employee);

    }

    @Test
    public void validEmployeeFields_resultsInPassingValidation() {
        employeeValidator.setupValidators();

        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withLname("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Tester").build();

        Assertions.assertThatCode(() -> employeeValidator.validate(employee))
                .doesNotThrowAnyException();
    }
}

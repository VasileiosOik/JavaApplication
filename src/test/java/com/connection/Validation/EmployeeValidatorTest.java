package com.connection.Validation;

import com.connection.validation.EmployeeValidator;
import com.connection.validation.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

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
}

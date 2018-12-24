package com.connection.validation;

import com.connection.customexception.NameValidatorException;
import com.connection.domain.Employee;

public class NameValidator implements Validator {
    @Override
    public void validate(Employee employee) {
        if (employee.getName().matches((".*\\d+.*"))) {
            throw new NameValidatorException("1", "Wrong name", "Name cannot contain numbers");
        }

    }
}

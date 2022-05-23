package com.connection.validation;

import com.connection.customexception.IllegalArgumentValidatorException;
import com.connection.domain.Employee;

public class NameValidator implements Validator {

    public static final String NAME_CHARACTERS_ONLY = ".*\\d+.*";

    @Override
    public void validate(Employee employee) {
        if (null != employee.getName() && employee.getName().matches(NAME_CHARACTERS_ONLY)) {
            throw new IllegalArgumentValidatorException("1", "Wrong name", "Name cannot contain numbers");
        }
    }
}

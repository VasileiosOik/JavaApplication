package com.connection.validation;


import com.connection.domain.Employee;

@FunctionalInterface
public interface Validator {
    void validate(Employee employee);
}

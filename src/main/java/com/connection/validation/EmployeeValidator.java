package com.connection.validation;

import com.connection.domain.Employee;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class EmployeeValidator {

    private Set<Validator> validators;

    public Set<Validator> getValidators() {
        return validators;
    }

    public void setValidators(Set<Validator> validators) {
        this.validators = validators;
    }

    @PostConstruct
    public void setupValidators() {
        validators = new HashSet<>();
        validators.add(new NameValidator());
        validators.add(new JobTitleValidator());
    }

    public void validate(Employee employee) {
        validators.forEach(p -> p.validate(employee));
    }

}

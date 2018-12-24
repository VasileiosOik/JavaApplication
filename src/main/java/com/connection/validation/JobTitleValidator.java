package com.connection.validation;

import com.connection.customexception.NameValidatorException;
import com.connection.domain.Employee;
import com.connection.domain.JobTitle;

public class JobTitleValidator implements Validator {
    @Override
    public void validate(Employee employee) {
        if (!(employee.getJobTitle().equalsIgnoreCase(JobTitle.DEVELOPER.toString()) || employee.getJobTitle().equalsIgnoreCase(JobTitle.TESTER.toString()))) {
            throw new NameValidatorException("2", "Wrong Job Title", "Job Title can be only Developer or Tester");
        }
    }
}

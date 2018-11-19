package com.connection.mapper;

import com.connection.domain.Employee;

import java.time.LocalDate;

public class EmployeeBuilder {

    private int id;
    private String name;
    private String lName;
    private String jobTitle;
    private LocalDate hireDate;
    private int managerId;
    private int departmentId;

    public EmployeeBuilder withId(int id) {

        this.id = id;
        return this;
    }

    public EmployeeBuilder withName(String name) {

        this.name = name;

        return this;  //By returning the builder each time, we can create a fluent interface.

    }

    public EmployeeBuilder withLname(String lName) {

        this.lName = lName;

        return this;

    }

    public EmployeeBuilder withJobTitle(String jobTitle) {

        this.jobTitle = jobTitle;

        return this;

    }

    public EmployeeBuilder withHireDate(LocalDate hireDate) {

        this.hireDate = hireDate;

        return this;

    }

    public EmployeeBuilder withManageId(int managerId) {

        this.managerId = managerId;

        return this;

    }

    public EmployeeBuilder withDepartmentId(int departmentId) {

        this.departmentId = departmentId;

        return this;

    }

    public Employee build() {

        Employee employee = new Employee();

        employee.setId(this.id);

        employee.setName(this.name);

        employee.setlName(this.lName);

        employee.setJobTitle(this.jobTitle);

        employee.setHireDate(this.hireDate);

        employee.setDepartmentId(this.departmentId);

        employee.setManagerId(this.managerId);

        return employee;

    }


}

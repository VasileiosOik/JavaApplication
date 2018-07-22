package com.connection.mapper;

import com.connection.domain.Employee;

public class EmployeeBuilder {

    private int id;
    private String name;
    private String lName;
    private String jobTitle;
    private String hireDate;
    private int managerId;
    private int departmentId;

    public EmployeeBuilder withId(int id) {

        this.id = id;
        return this;
    }

    public EmployeeBuilder withName(String name){

        this.name = name;

        return this;  //By returning the builder each time, we can create a fluent interface.

    }

    public EmployeeBuilder withLname(String lname){

        this.lName = lname;

        return this;

    }

    public EmployeeBuilder withJobTitle(String jobTitle){

        this.jobTitle = jobTitle;

        return this;

    }

    public EmployeeBuilder withHireDate(String hireDate){

        this.hireDate = hireDate;

        return this;

    }

    public EmployeeBuilder withManageId(int managerId){

        this.managerId = managerId;

        return this;

    }

    public EmployeeBuilder withDepartmentId(int departmentId){

        this.departmentId = departmentId;

        return this;

    }

    public Employee build(){

        Employee employee = new Employee();

        employee.setId(this.id);

        employee.setName(this.name);

        employee.setLname(this.lName);

        employee.setJobTitle(this.jobTitle);

        employee.setHiredate(this.hireDate);

        employee.setDepartmentId(this.departmentId);

        employee.setManagerId(this.managerId);

        return employee;

    }


}
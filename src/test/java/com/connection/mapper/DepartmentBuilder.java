package com.connection.mapper;


import com.connection.domain.Department;

public class DepartmentBuilder {

    private int depId;
    private String depName;


    public DepartmentBuilder withId(int depId) {

        this.depId = depId;
        return this;
    }

    public DepartmentBuilder withName(String depName){

        this.depName = depName;

        return this;

    }

    public Department build(){

        Department department = new Department();

        department.setDepId(this.depId);

        department.setDepName(this.depName);

        return department;

    }
}

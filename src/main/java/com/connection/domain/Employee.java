package com.connection.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Employee {
	@ApiModelProperty(notes = "The Employee Id")
	private int id;
	@ApiModelProperty(notes = "The Employee name")
	private String name;
	@ApiModelProperty(notes = "The Employee last name")
	private String lName;
	@ApiModelProperty(notes = "The Employee job title")
	private String jobTitle;
	@ApiModelProperty(notes = "The Employee Hire Date")
	private String hireDate;
	@ApiModelProperty(notes = "The Employee's manager Id")
	private int managerId;
	@ApiModelProperty(notes = "The Employee's department Id")
	private int departmentId;

	public Employee() {
		//empty constructor is needed
	}

	public Employee(int id, String name, String lName, String jobTitle, String hireDate, int managerId,
			int departmentId) {
		super();
		this.id = id;
		this.name = name;
		this.lName = lName;
		this.jobTitle = jobTitle;
		this.hireDate = hireDate;
		this.managerId = managerId;
		this.departmentId = departmentId;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}

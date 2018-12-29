package com.connection.domain;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Department {
    @ApiModelProperty(notes = "The department's id")
    private int depId;
    @ApiModelProperty(notes = "The department's name")
    private String depName;

    public Department() {
        //empty constructor is needed
    }

    public Department(int depId, String depName) {
        super();
        this.depId = depId;
        this.depName = depName;
    }

    public int getDepId() {
        return depId;
    }

    public void setDepId(int depId) {
        this.depId = depId;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

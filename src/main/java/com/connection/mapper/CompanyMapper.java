package com.connection.mapper;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CompanyMapper {

    List<Employee> getAllEmployees();

    List<Department> getAllDepartments();

    List<Employee> getEmployeesInSpecificDepartment(@Param("depName") String depName);

    List<Employee> getAllEmployeesByTheirManager(@Param("name") String name, @Param("lName") String lName);

    List<Employee> getEmployeesByNumOfYearsWorked(@Param("numOfYears") int numOfYears);

    void changeEmployeeJobTitle(@Param("name") String name, @Param("lName") String lName,
                                @Param("jobTitle") String jobTitle);

    int removeEmployee(@Param("id") int id);

    void removeDepartment(@Param("depName") String depName);

    void updateEmployeeDataBeforeDeleteOfDepartment(@Param("depName") String depName);

    void addDepartment(@Param("department") Department department);

    List<Employee> changeAnEmployeeDepartment(@Param("name") String name, @Param("lName") String lName,
                                              @Param("depName") String departmentName);

    void changeAnEmployeeDepartmentAndCheckIfManager(@Param("name") String name, @Param("lName") String lName,
                                                     @Param("depName") String departmentName);

    void addEmployee(@Param("employee") Employee employee);

    int getMaxEmployeeId();

    int getMaxDepartmentId();

    Department verifyDepartmentExistence(@Param("depName") String depName);

    Employee verifyEmployeeExistence(@Param("id") int id);

    void updateEmployeeDepartmentId(@Param("depName") String depName);

    Employee getAnEmployee(@Param("id") int id);

    void updateAnEmployee(@Param("id") int id, @Param("employee") Employee employee);

}

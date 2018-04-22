package com.connection.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.connection.domain.Department;
import com.connection.domain.Employee;

public interface ConnectionMapper {

	List<Employee> showAllEmployees();

	List<Department> showAllDepartments();

	List<Employee> employeesInSpecificDepartment(@Param("depName") String depName);

	List<Employee> getAllEmployeesByTheirManager(@Param("name") String name, @Param("lName") String lName);

	List<Employee> getEmployeesByNumOfYearsWorked(int numOfYears);

	void changeEmployeeJobTitle(@Param("name") String name, @Param("lName") String lName,
			@Param("jobTitle") String jobTitle);

	void removeEmployee(@Param("name") String name, @Param("lName") String lName);

	void removeDepartment(@Param("depName") String depName);

	void removeEmployeeThatIsManager();

	void updateEmployeeDataBeforeDeleteOfDepartment(@Param("depName") String depName);

	void addDepartment(Department department);

	List<Employee> changeAnEmployeeDepartment(@Param("name") String name, @Param("lName") String lName,
			@Param("depName") String departmentName);

	Employee changeAnEmployeeDepartmentAndCheckIfManager(@Param("name") String name, @Param("lName") String lName,
			@Param("depName") String departmentName);

	String departmentFound(@Param("depId") int depId);

	void addEmployee(Employee employee);

	Employee returnDepartmentIdAndManagerId(@Param("name") String name, @Param("lName") String lName);

	int managerOfADepartment(@Param("depId") int depId);

	int getMaxEmployeeId();

	int getMaxDepartmentId();

	int verifyDepartmentExistence(@Param("depName") String depName);

	Employee verifyIfEmployeeExists(@Param("id") int id);

	void updateEmployee(@Param("depName") String depName);

}

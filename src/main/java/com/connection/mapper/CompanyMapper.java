package com.connection.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyMapper {

	List<Employee> showAllEmployees();

	List<Department> showAllDepartments();

	List<Employee> employeesInSpecificDepartment(@Param("depName") String depName);

	List<Employee> getAllEmployeesByTheirManager(@Param("name") String name, @Param("lName") String lName);

	List<Employee> getEmployeesByNumOfYearsWorked(@Param("numOfYears") int numOfYears);

	void changeEmployeeJobTitle(@Param("name") String name, @Param("lName") String lName,
			@Param("jobTitle") String jobTitle);

	int removeEmployee(@Param("id") int id);

	int removeDepartment(@Param("depName") String depName);

	void removeEmployeeThatIsManager();

	void updateEmployeeDataBeforeDeleteOfDepartment(@Param("depName") String depName);

	void addDepartment(@Param("department") Department department);

	List<Employee> changeAnEmployeeDepartment(@Param("name") String name, @Param("lName") String lName,
			@Param("depName") String departmentName);

	Employee changeAnEmployeeDepartmentAndCheckIfManager(@Param("name") String name, @Param("lName") String lName,
			@Param("depName") String departmentName);

	String departmentFound(@Param("depId") int depId);

	void addEmployee(@Param("employee") Employee employee);

	Employee returnDepartmentIdAndManagerId(@Param("name") String name, @Param("lName") String lName);

	int managerOfADepartment(@Param("depId") int depId);

	int getMaxEmployeeId();

	int getMaxDepartmentId();

	Department verifyDepartmentExistence(@Param("depName") String depName);

	Employee verifyIfEmployeeExists(@Param("id") int id);

	void updateEmployee(@Param("depName") String depName);

	Employee getAnEmployee(@Param("id") int id);

	void updateAnEmployee(@Param("id") int id, @Param("employee") Employee employee);

}

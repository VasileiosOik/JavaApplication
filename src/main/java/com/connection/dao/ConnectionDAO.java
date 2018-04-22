package com.connection.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.ConnectionMapper;

@Component
@MapperScan("com.connection.mapper")
public class ConnectionDAO {

	private final Logger log = LoggerFactory.getLogger(ConnectionDAO.class);

	@Autowired
	private ConnectionMapper connectionMapper;

	public List<Employee> getEmployeesByNumOfYearsWorked(int numOfYears) {

		List<Employee> listEmp = connectionMapper.getEmployeesByNumOfYearsWorked(numOfYears);

		if (!listEmp.isEmpty()) {
			log.debug("Get employees by their years : {}", listEmp);
		} else {
			log.debug("No employees found");
		}
		return listEmp;
	}

	public void removeEmployee(String name, String lName) {

		List<Employee> listEmp = connectionMapper.showAllEmployees();

		if (!listEmp.isEmpty()) {
			for (Employee employee : listEmp) {
				if (employee.getName().equals(name) && employee.getLname().equals(lName)) {
					connectionMapper.removeEmployee(name, lName);
					log.debug("Employee removed successfully");
					log.debug("Checking if employee was a manager...");
					removeEmployeeThatIsManager(listEmp, employee);
				}
			}
		} else {
			log.debug("No employee found with this name");
		}
	}

	private void removeEmployeeThatIsManager(List<Employee> listEmp, Employee employee) {
		for (Employee emp : listEmp) {
			if (emp.getManagerId() == employee.getId()) {
				connectionMapper.removeEmployeeThatIsManager();
				log.debug("Employee was a manager so the Employees data are updated accordingly");
				break;
			}
		}
	}

	public List<Employee> getAllEmployees() {

		List<Employee> listEmp = connectionMapper.showAllEmployees();

		if (!listEmp.isEmpty()) {
			log.debug("Get all employees : {}", listEmp);
		} else {
			log.debug("No employees found");
		}
		return listEmp;
	}

	public List<Department> getAllDepartments() {
		List<Department> departments = connectionMapper.showAllDepartments();

		if (!departments.isEmpty()) {
			log.info("Get all departments : {}", departments);
		} else {
			log.info("No Departments to display");
		}
		return departments;
	}

	public List<Employee> getAllEmployeesInADepartment(String name) {

		List<Employee> employees = connectionMapper.employeesInSpecificDepartment(name);

		if (!employees.isEmpty()) {
			log.debug("Department found!%n");
			log.info("Displaying Employees in it: {}%n", employees);
		} else {
			log.debug("No department found with such a name");
		}
		return employees;
	}

	public List<Employee> getAllEmployeesByTheirManagers(String name, String lastName) {

		List<Employee> employees = connectionMapper.getAllEmployeesByTheirManager(name, lastName);
		if (!employees.isEmpty()) {
            log.debug("Manager found!%n");
            log.info("Displaying Employees in it: {}%n", employees);
            return employees;
		} else {
			log.debug("No Manager found with such a name");
			return Collections.emptyList();
		}
	}

	public void changeEmployeeJobTitle(String name, String lName, String jobTitle) {

		List<Employee> results = connectionMapper.showAllEmployees();
		if (!results.isEmpty()) {
			for (Employee employee : results) {
				if (employee.getName().equals(name) && employee.getLname().equals(lName)) {
					connectionMapper.changeEmployeeJobTitle(name, lName, jobTitle);
					log.debug("Employee's job title has been updated successfully!");
				}
			}
		} else {
			log.debug("No Employees found to change a job title");
		}

	}

	public void addDepartment(Department department) {
		if (verifyDepartmentExistence(department.getDepName()) == 0) {
			log.debug("Cannot add department with the same name");
		} else {
			connectionMapper.addDepartment(department);
			log.debug("Department was saved successfully!");
		}
	}

	public void changeAnEmployeeDepartment(String name, String lName, String departmentName) {

		List<Employee> listEmp = connectionMapper.changeAnEmployeeDepartment(name, lName, departmentName);

		if (listEmp.isEmpty()) {
			connectionMapper.changeAnEmployeeDepartmentAndCheckIfManager(name, lName, departmentName);
			log.debug("Employee data has been updated successfully!");
		} else {
			log.debug("The current employee is a manager and cannot be transfered");
		}
	}

	// add an employee
	public void addEmployee(Employee employee) {
		if (addEmployeeEnchanced(employee.getManagerId(), employee.getDepartmentId())) {
			employee.setHiredate(determineTheHireDate(employee.getHiredate()));
			connectionMapper.addEmployee(employee);
			log.debug("Employee was saved successfully!");
		}
	}

	// checking 1
	private boolean addEmployeeEnchanced(int managerId, int departmentId) {
		if (verifyDepartmentExistence(connectionMapper.departmentFound(departmentId)) == 0
				&& (connectionMapper.managerOfADepartment(departmentId)) == managerId) {
			log.debug("Both exists under the same section in the system.");
			return true;
		} else {
			log.debug("They are not in the same section in the system"
					+ "Therefore an employee cannot be added. Please try again");
			return false;
		}
	}

	// checking 2
	private String determineTheHireDate(String hireDate) {

		// hire date checking
		Boolean dateStory = false;
		Date date1;
		do {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			try {
				date1 = sdf.parse(hireDate);
				// 1st check
				dateStory = date1.before(new Date());
				// 2nd check
				// convert date to calendar
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				// manipulate date
				c.add(Calendar.DATE, 30);
				// convert calendar to date
				Date currentDatePlusOne = c.getTime();

				if (date1.before(currentDatePlusOne)) {
					dateStory = true;
				} else {
					log.debug("The date is more than one month in front");
				}
			} catch (ParseException e) {
				log.debug("The hired date is not in a valid format");
			}
		} while (!dateStory);

		return hireDate;
	}

	// max+1 for employees id
	public int getMaxEmployeeId() {
		log.debug("The id of the new employee wil be: [{}]", connectionMapper.getMaxEmployeeId() + 1);
		return connectionMapper.getMaxEmployeeId();
	}

	// max+1 for the departments id
	public int getMaxDepartmentId() {
		log.debug("The id of the new department wil be: [{}]", connectionMapper.getMaxDepartmentId() + 1);
		return connectionMapper.getMaxDepartmentId();
	}

	public int verifyDepartmentExistence(String name) {
		List<Department> departments = connectionMapper.showAllDepartments();
		if (!departments.isEmpty()) {
			for (Department department : departments) {
				if (department.getDepName().equals(name)) {
					log.debug("Department exists.");
					return 0;
				}
			}
		}
		log.debug("Department does not exist");
		return 1;
	}

	public Employee verifyEmployeeExistence(Integer id) {
		return connectionMapper.verifyIfEmployeeExists(id);

	}

	public void removeDepartment(String name) {

		if (verifyDepartmentExistence(name) == 0) {
			// because of the foreign keys constrains first you remove the
			// department and second you remove the employee
			connectionMapper.updateEmployee(name);
			connectionMapper.removeDepartment(name);
			log.info("Department removed successfully");
		} else {
			log.info("Department does not exist");
		}
	}
}

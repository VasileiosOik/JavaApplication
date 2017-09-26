package com.connection.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.connection.configuration.DatabaseConfig;
import com.connection.dao.MongoDAO;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { MongoDAO.class, DatabaseConfig.class })
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class MongoDAOIT {

	@Autowired
	private MongoDAO mongoDAO;

	public static final Logger LOG = LoggerFactory.getLogger(MongoDAOIT.class);

	@Test
	@UsingDataSet(locations = "initialJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	@ShouldMatchDataSet(location = "afterDepFile.json")
	public void addDepartmentData() {
		
		mongoDAO.addDepartmentToMongoDB(getMockDepartment());

	}

	@Test
	@UsingDataSet(locations = "initialJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	@ShouldMatchDataSet(location = "afterEmpFile.json")
	public void addEmployeeData() {

		mongoDAO.addEmployeeToMongoDB(getOneMockEmployee());

	}

	@Test
	public void testReturnDateBetweenDates() throws ParseException {

		mongoDAO.returnDateBetweenDates(getMockDateStart(), getMockDateEnd());

	}

	private Date getMockDateStart() throws ParseException {
		String dateInString = "30/08/2016";
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date date = formatter.parse(dateInString);
		return date;
	}

	private Date getMockDateEnd() throws ParseException {
		String dateInString = "30/11/2019";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = formatter.parse(dateInString);
		return date;

	}

	private Employee getOneMockEmployee() {
		Employee employee = new Employee();
		employee.setName("Alex");
		employee.setLname("Tso");
		employee.setJobTitle("Tester");
		employee.setId(100015);
		employee.setManagerId(100004);
		employee.setDepartmentId(1003);
		employee.setHiredate("2016-05-29");

		return employee;

	}

	private Department getMockDepartment() {
		Department department = new Department();
		department.setDepId(1003);
		department.setDepName("Technology");

		return department;

	}

}

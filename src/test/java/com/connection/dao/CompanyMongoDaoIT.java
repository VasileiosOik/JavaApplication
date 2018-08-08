package com.connection.dao;

import com.connection.configuration.MongoDBConfiguration;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CompanyMongoDao.class, MongoDBConfiguration.class })
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class CompanyMongoDaoIT {

	@Autowired
	private CompanyMongoDao companyMongoDao;

	@Test
	@UsingDataSet(locations = "initialJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	@ShouldMatchDataSet(location = "mongoTestData/afterDepFile.json")
	public void addDepartmentData() {
		
		companyMongoDao.addDepartmentToMongoDB(getMockDepartment());

	}

	@Test
	@UsingDataSet(locations = "initialJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	@ShouldMatchDataSet(location = "mongoTestData/afterEmpFile.json")
	public void addEmployeeData() {

		companyMongoDao.addEmployeeToMongoDB(getOneMockEmployee());

	}

	@Test
	public void testReturnDateBetweenDates() throws ParseException {

		companyMongoDao.returnDateBetweenDates(getMockDateStart(), getMockDateEnd());

	}

	private Date getMockDateStart() throws ParseException {
		String dateInString = "30/08/2016";
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.parse(dateInString);
	}

	private Date getMockDateEnd() throws ParseException {
		String dateInString = "30/11/2019";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.parse(dateInString);
	}

	private Employee getOneMockEmployee() {
		Employee employee = new Employee();
		employee.setName("Alex");
		employee.setlName("Tso");
		employee.setJobTitle("Tester");
		employee.setId(100015);
		employee.setManagerId(100004);
		employee.setDepartmentId(1003);
		employee.setHireDate(LocalDate.of(2016, Month.MAY, 29 ));

		return employee;

	}

	private Department getMockDepartment() {
		Department department = new Department();
		department.setDepId(1003);
		department.setDepName("Technology");

		return department;

	}

}

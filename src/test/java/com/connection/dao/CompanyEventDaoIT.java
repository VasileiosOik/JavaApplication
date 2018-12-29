package com.connection.dao;

import com.connection.configuration.MongoDBConfiguration;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.DepartmentBuilder;
import com.connection.mapper.EmployeeBuilder;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbConfigurationBuilder;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CompanyEventDao.class, MongoDBConfiguration.class})
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class CompanyEventDaoIT {

    @Autowired
    private CompanyEventDao companyEventDao;

    @Rule
    public MongoDbRule remoteMongoDbRule = new MongoDbRule(MongoDbConfigurationBuilder.mongoDb().databaseName("Company").port(27017).host("localhost").build());


    @Test
    @UsingDataSet(locations = "/mongoTestData/initialJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    //@ShouldMatchDataSet(location = "/mongoTestData/afterEmpFile.json")
    public void addEmployeeToMongoDB() {
        Employee employee = new EmployeeBuilder()
                .withDepartmentId(1003)
                .withHireDate(LocalDate.of(2016, Month.MAY, 29))
                .withId(100015)
                .withJobTitle("Tester")
                .withName("Alex")
                .withLname("dean")
                .withManageId(100004).build();
        companyEventDao.addEmployeeToMongoDB(employee);
    }

    @Test
    @UsingDataSet(locations = "/mongoTestData/initialJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    //@ShouldMatchDataSet(location = "/mongoTestData/afterDepFile.json")
    public void addDepartmentToMongoDB() {
        Department department = new DepartmentBuilder().withName("Finance").withId(1005).build();
        companyEventDao.addDepartmentToMongoDB(department);
    }

    @Test
    @UsingDataSet(locations = "/mongoTestData/datesJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void returnEventsBetweenDates() {
        ResponseEntity<Object> dateBetweenDates = companyEventDao.getEventsBetweenDates(getMockDateStart(), getMockDateEnd());
        assertEquals(200, dateBetweenDates.getStatusCode().value());
    }

    private LocalDate getMockDateStart() {
        String dateInString = "2016-08-30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateInString, formatter);
    }

    private LocalDate getMockDateEnd() {
        String dateInString = "2019-11-30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateInString, formatter);
    }
}

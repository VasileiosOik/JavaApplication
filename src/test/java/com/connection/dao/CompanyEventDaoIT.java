package com.connection.dao;

import com.connection.configuration.MongoDBConfiguration;
import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.connection.mapper.DepartmentBuilder;
import com.connection.mapper.EmployeeBuilder;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CompanyEventDao.class, MongoDBConfiguration.class})
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class CompanyEventDaoIT {

    @Value("classpath:mongoTestData/datesJsonFile.json")
    private Resource resource;

    @Autowired
    private CompanyEventDao companyEventDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @After
    public void tearUp() {
        mongoTemplate.remove(new Query(), "Company");
    }

    @Test
    public void addEmployeeToMongoDB() {
        Employee employee = new EmployeeBuilder()
                .withDepartmentId(1003)
                .withHireDate(LocalDate.of(2016, Month.DECEMBER, 29))
                .withId(100015)
                .withJobTitle("Tester")
                .withName("Alex")
                .withLname("dean")
                .withManageId(100004).build();
        companyEventDao.addEmployeeToMongoDB(employee);

        List<Map> map = mongoTemplate.find(Query.query(Criteria.where("firstName").is("Alex")), Map.class, "Company");
        System.err.println(map.get(0));
        assertNotNull(map);
    }

    @Test
    public void addDepartmentToMongoDB() {
        Department department = new DepartmentBuilder().withName("Finance").withId(1005).build();
        companyEventDao.addDepartmentToMongoDB(department);

        List<Map> map = mongoTemplate.find(Query.query(Criteria.where("departmentName").is("Finance")), Map.class, "Company");
        assertNotNull(map);
    }

    @Test
    public void returnEventsBetweenDates() throws ParseException, IOException {
        BasicDBObject firstEmployee = new BasicDBObject();
        firstEmployee.append("employeeId", 100015).append("firstName", "Alex")
                .append("lastName", "dean").append("jobTitle", "Tester")
                .append("hireDate", Date.from(LocalDate.of(2016, 5, 29).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("managerId", 100004)
                .append("departmentId", 1003)
                .append("timeCreated", Date.from(LocalDate.of(2018, 9, 2).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        BasicDBObject secondEmployee = new BasicDBObject();
        secondEmployee.append("employeeId", 100016).append("firstName", "Bill")
                .append("lastName", "Eco").append("jobTitle", "Dev")
                .append("hireDate", Date.from(LocalDate.of(2016, 5, 29).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("managerId", 100004)
                .append("departmentId", 1003)
                .append("timeCreated", Date.from(LocalDate.of(2018, 11, 10).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        mongoTemplate.save(new Document(firstEmployee), "Company");
        mongoTemplate.save(new Document(secondEmployee), "Company");

        ResponseEntity<Object> dateBetweenDates = companyEventDao.getEventsBetweenDates(getMockDateStart(), getMockDateEnd());
        assertEquals(200, dateBetweenDates.getStatusCode().value());
    }

    private Date getMockDateStart() throws ParseException {
        String dateInString = "2016-08-30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(dateInString);
    }

    private Date getMockDateEnd() throws ParseException {
        String dateInString = "2019-11-30";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(dateInString);
    }
}

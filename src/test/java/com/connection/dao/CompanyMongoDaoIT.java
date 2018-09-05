package com.connection.dao;

import com.connection.configuration.CompanyMongoDbRule;
import com.connection.configuration.MongoDBConfiguration;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CompanyMongoDao.class, MongoDBConfiguration.class })
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class CompanyMongoDaoIT {

	@Autowired
	private CompanyMongoDao companyMongoDao;

	@Rule
    public MongoDbRule remoteMongoDbRule = new CompanyMongoDbRule();

	@Test
    @UsingDataSet(locations = "/mongoTestData/unitsJsonFile.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testReturnDateBetweenDates() {

        List<Map> dateBetweenDates = companyMongoDao.returnDateBetweenDates(getMockDateStart(), getMockDateEnd());
        assertEquals(2, dateBetweenDates.size());

    }

	private LocalDate getMockDateStart() {
		String dateInString = "2016-08-30";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(dateInString,formatter);
	}

	private LocalDate getMockDateEnd() {
		String dateInString = "2019-11-30";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(dateInString,formatter);
	}
}

package com.connection.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

@Component
public class CompanyMongoDao {

	private static final Logger LOG = LoggerFactory.getLogger(CompanyMongoDao.class);

	private static final String COMPANY = "Company";
	private static final String CREATED_TIME = "timeCreated";

	@Autowired
	private MongoTemplate mongoTemplate;

	public void addDepartmentToMongoDB(Department department) {
		if (!mongoTemplate.collectionExists(COMPANY)) {
			mongoTemplate.createCollection(COMPANY);
		}

		DBCollection dbCollection = mongoTemplate.getCollection(COMPANY);
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.append("DepartmentId", department.getDepId()).append("DepartmentName", department.getDepName())
				.append(CREATED_TIME, new Date());
		dbCollection.insert(basicDBObject);
		LOG.debug("The department document has been added successfully");
	}

	public void addEmployeeToMongoDB(Employee emp) {
		if (!mongoTemplate.collectionExists(COMPANY)) {
			mongoTemplate.createCollection(COMPANY);
		}

		DBCollection dbCollection = mongoTemplate.getCollection(COMPANY);
		BasicDBObject basicDBObject = new BasicDBObject();
		basicDBObject.append("EmployeeId", emp.getId()).append("FirstName", emp.getName())
				.append("LastName", emp.getLname()).append("JobTitle", emp.getJobTitle())
				.append("HireDate", emp.getHiredate()).append("ManagerId", emp.getManagerId())
				.append("DepartmentId", emp.getDepartmentId()).append(CREATED_TIME, new Date());
		dbCollection.insert(basicDBObject);
		LOG.debug("The employee document has been added successfully");
	}

	public void returnDateBetweenDates(Date fromDate, Date toDate) {
		if (mongoTemplate.collectionExists(COMPANY)) {

			DBCollection dbCollection = mongoTemplate.getCollection(COMPANY);

			BasicDBObject query = new BasicDBObject();
			query.put(CREATED_TIME, BasicDBObjectBuilder.start("$gte", fromDate).add("$lte", toDate).get());
			DBCursor eventsList = dbCollection.find(query).sort(new BasicDBObject(CREATED_TIME, -1));

			LOG.debug("Documents size {}: ", eventsList.count());

			while (eventsList.hasNext()) {
				LOG.info("List with events: {}", eventsList.next());
			}

		}

	}
}

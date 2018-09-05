package com.connection.dao;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CompanyMongoDao {

	private static final Logger LOG = LoggerFactory.getLogger(CompanyMongoDao.class);

	private static final String COMPANY = "Company";
	private static final String CREATED_TIME = "timeCreated";

	private final MongoTemplate mongoTemplate;

    @Autowired
    public CompanyMongoDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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
				.append("LastName", emp.getlName()).append("JobTitle", emp.getJobTitle())
				.append("HireDate", Date.from(emp.getHireDate().atStartOfDay(ZoneId.systemDefault()).toInstant())).append("ManagerId", emp.getManagerId())
				.append("DepartmentId", emp.getDepartmentId()).append(CREATED_TIME, new Date());
		dbCollection.insert(basicDBObject);
		LOG.debug("The employee document has been added successfully");
	}

	public List<Map> returnDateBetweenDates(LocalDate fromDate, LocalDate toDate) {
		if (mongoTemplate.collectionExists(COMPANY)) {
            Criteria criteria = this.eventsCriteria(fromDate, toDate);
			Pageable pageable = new PageRequest(0,1000, new Sort(Sort.Direction.DESC, CREATED_TIME));
            Query query = Query.query(criteria).with(pageable);
            List<Map> events = this.mongoTemplate.find(query, Map.class, COMPANY);
            if (!CollectionUtils.isEmpty(events)) {
                return events;
            }
		}
        return Collections.emptyList();
    }

    private Criteria eventsCriteria(LocalDate fromDate, LocalDate toDate) {
        Criteria criteria = new Criteria();

        if (null != fromDate && null != toDate) {
            criteria.and(CREATED_TIME).gte(fromDate).lte(toDate);
        }
        return  criteria;
    }
}

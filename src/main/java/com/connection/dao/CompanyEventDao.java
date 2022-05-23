package com.connection.dao;

import com.connection.domain.Department;
import com.connection.domain.Employee;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class CompanyEventDao {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyEventDao.class);

    private static final String COMPANY = "Company";
    private static final String CREATED_TIME = "timeCreated";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CompanyEventDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void addDepartmentToMongoDB(Department department) {
        if (!mongoTemplate.collectionExists(COMPANY)) {
            mongoTemplate.createCollection(COMPANY);
        }

        MongoCollection<Document> dbCollection = mongoTemplate.getCollection(COMPANY);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("departmentId", department.getDepId())
                .append("departmentName", department.getDepName())
                .append(CREATED_TIME, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dbCollection.insertOne(new Document(basicDBObject));
        LOG.debug("The department document has been added successfully");
    }

    public void addEmployeeToMongoDB(Employee emp) {
        if (!mongoTemplate.collectionExists(COMPANY)) {
            mongoTemplate.createCollection(COMPANY);
        }

        MongoCollection<Document> dbCollection = mongoTemplate.getCollection(COMPANY);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("employeeId", emp.getId()).append("firstName", emp.getName())
                .append("lastName", emp.getlName()).append("jobTitle", emp.getJobTitle())
                .append("hireDate", Date.from(emp.getHireDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .append("managerId", emp.getManagerId())
                .append("departmentId", emp.getDepartmentId()).append(CREATED_TIME, Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        dbCollection.insertOne(new Document(basicDBObject));
        LOG.debug("The employee document has been added successfully");
    }

    public ResponseEntity<Object> getEventsBetweenDates(Date fromDate, Date toDate) {
        if (!mongoTemplate.collectionExists(COMPANY)) {
            mongoTemplate.createCollection(COMPANY);
        }
        Criteria criteria = this.eventsCriteria(fromDate, toDate);
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, CREATED_TIME));
        Query query = Query.query(criteria).with(pageable);
        List<Map> events = this.mongoTemplate.find(query, Map.class, COMPANY);
        LOG.debug("The events are: {}", events);
        if (!CollectionUtils.isEmpty(events)) {
            return new ResponseEntity<>(events, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        }
    }

    private Criteria eventsCriteria(Date fromDate, Date toDate) {
        Criteria criteria = new Criteria();

        if (null != fromDate && null != toDate) {
            criteria.and(CREATED_TIME).gte(fromDate).lte(toDate);
        }
        return criteria;
    }
}

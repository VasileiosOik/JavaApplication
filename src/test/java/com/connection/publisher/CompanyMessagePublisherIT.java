package com.connection.publisher;

import com.connection.application.Application;
import com.connection.domain.Employee;
import com.connection.mapper.EmployeeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.time.LocalDate;
import java.time.Month;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:test.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class CompanyMessagePublisherIT {

    @Autowired
    private CompanyMessagePublisher companyMessagePublisher;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Before
    public void setUp() {
        companyMessagePublisher.setEvent("test.event");
        amqpAdmin.purgeQueue("man.queue", false);
    }

    @After
    public void clearUp() {
        amqpAdmin.purgeQueue("man.queue", false);
    }
//not completed yet
    @Test
    public void sendMessageToQueue() {
        Employee employee = new EmployeeBuilder()
                .withName("Bill")
                .withName("Eco")
                .withId(1)
                .withDepartmentId(1001)
                .withManageId(100015)
                .withHireDate(LocalDate.of(1988, Month.OCTOBER, 23))
                .withJobTitle("Developer").build();

        companyMessagePublisher.publish(employee);
    }
}

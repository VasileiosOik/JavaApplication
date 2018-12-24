package com.connection.Publisher;

import com.connection.application.Application;
import com.connection.domain.Employee;
import com.connection.mapper.EmployeeBuilder;
import com.connection.publisher.ActionMessagePublisher;
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
public class ActionMessagePublisherIT {

    @Autowired
    private ActionMessagePublisher actionMessagePublisher;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Before
    public void setUp() {
        actionMessagePublisher.setEvent("test.event");
        amqpAdmin.purgeQueue("simple.queue.bill", false);
    }

    @After
    public void clearUp() {
        amqpAdmin.purgeQueue("simple.queue.bill", false);
    }

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

        actionMessagePublisher.publish(employee);
    }
}

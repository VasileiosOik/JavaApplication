package com.connection.publisher;

import com.connection.domain.CustomMessage;
import com.connection.domain.Employee;
import com.connection.domain.Payload;
import com.connection.messagehandlers.MessageCreator;
import com.connection.messagehandlers.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ActionMessagePublisher {

    private final MessageCreator messageCreator;

    private final MessagePublisher<CustomMessage> messagePublisher;

    private static final Logger LOG = LoggerFactory.getLogger(ActionMessagePublisher.class);

    @Value("${ext.received.event}")
    private String event;

    @Autowired
    public ActionMessagePublisher(MessageCreator messageCreator, MessagePublisher<CustomMessage> messagePublisher) {
        this.messageCreator = messageCreator;
        this.messagePublisher = messagePublisher;
    }

    public void publish(Employee employee) {
        CustomMessage customMessage = constructMessage(event, employee);

        messagePublisher.sendMessage(customMessage);
        LOG.debug("Published Message");
    }

    private CustomMessage constructMessage(String event, Employee employee) {
        return messageCreator.constructMessage(event, new Payload<>(employee));
    }

    public void setEvent(String event) {
        this.event = event;
    }
}

package com.customer.profiler.worker;

import com.customer.profiler.exception.ProfilerException;
import com.customer.profiler.service.ProductValidationService;
import com.customer.profiler.service.models.CustomerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQDeadLetterConsumer {

    private static Logger LOGGER = LoggerFactory.getLogger(RabbitMQDeadLetterConsumer.class);

    @Autowired
    private ProductValidationService productValidationService;

    @RabbitListener(queues = "${deadLetter.rabbitmq.queue}")
    public void processFailedMessages(CustomerProfile customerProfile) {
        try {
            LOGGER.info("Dead Letter : "+customerProfile.getProfileId());
            productValidationService.handleFailedValidation(customerProfile.getProfileId());
            LOGGER.info("Dead Letter Handled: "+customerProfile.getProfileId());
        } catch (ProfilerException e) {
            e.printStackTrace();
        }
    }

}

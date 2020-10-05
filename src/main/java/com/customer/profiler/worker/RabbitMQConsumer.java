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
public class RabbitMQConsumer {

    public static Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @Autowired
    private ProductValidationService productValidationService;

    @RabbitListener(queues = "${profiles.rabbitmq.queue}")
    public void recievedMessage(CustomerProfile customerProfile) throws ProfilerException {
        try {
            LOGGER.info("Recieved Message for validation: " + customerProfile.getProfileId());
            productValidationService.validate(customerProfile.getProfileId());
            LOGGER.info("Product validation complete: " + customerProfile.getProfileId());
        }catch (ProfilerException profilerException){
            LOGGER.info("Product validation failed: " + customerProfile.getProfileId());
            profilerException.printStackTrace();
            throw profilerException;
        }catch(Exception exception){
            LOGGER.info("Product validation failed: " + customerProfile.getProfileId());
            exception.printStackTrace();
            throw new ProfilerException(exception.getMessage());
        }
    }
}
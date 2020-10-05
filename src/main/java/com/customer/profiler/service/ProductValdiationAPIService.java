package com.customer.profiler.service;


import com.customer.profiler.exception.ProfilerException;
import com.customer.profiler.service.models.CustomerProfile;
import com.customer.profiler.service.models.ValidationResponse;
import com.customer.profiler.service.models.ValidationStatus;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@EnableHystrix
public class ProductValdiationAPIService {

    private static Logger log = LoggerFactory.getLogger(ProductValdiationAPIService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Async("asyncExecutor")
    @HystrixCommand(fallbackMethod = "reliable")
    public CompletableFuture<ValidationResponse> validate(CustomerProfile customerProfile, String productUri) throws InterruptedException, ProfilerException {
        log.info("getEmployeeName starts");
        ResponseEntity<ValidationResponse> responseEntity = restTemplate.postForEntity(productUri,customerProfile,ValidationResponse.class);
        ValidationResponse validationResponse = responseEntity.getBody();
        if(!responseEntity.getStatusCode().equals(HttpStatus.OK)){
            throw new ProfilerException(productUri+" error invoking product api status code : "+responseEntity.getStatusCode().toString());
        }
        return CompletableFuture.completedFuture(validationResponse);
    }

    public CompletableFuture<ValidationResponse> reliable(){
        ValidationResponse validationResponse = new ValidationResponse();
        validationResponse.setValidationStatus(ValidationStatus.FAILED);
        return CompletableFuture.completedFuture(validationResponse);
    }
}

package com.customer.profiler.service;

import com.customer.profiler.exception.ProfilerException;
import com.customer.profiler.service.models.CustomerProfile;
import org.springframework.cache.annotation.CachePut;

public interface IValidationService {
    @CachePut(value = "profiles",key = "#profileId")
    CustomerProfile validate(Integer profileId) throws ProfilerException;

    @CachePut(value = "profiles",key = "#profileId")
    CustomerProfile handleFailedValidation(Integer profileId) throws ProfilerException;
}

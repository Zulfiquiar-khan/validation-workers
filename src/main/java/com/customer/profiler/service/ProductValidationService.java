package com.customer.profiler.service;

import com.customer.profiler.dao.models.Profile;
import com.customer.profiler.dao.models.SubcribedProduct;
import com.customer.profiler.dao.repo.ProductRepository;
import com.customer.profiler.dao.repo.ProfileRepository;
import com.customer.profiler.dao.repo.SubscribedProductRespository;
import com.customer.profiler.exception.ProfilerException;
import com.customer.profiler.mapper.ObjectMapper;
import com.customer.profiler.service.models.CustomerProfile;
import com.customer.profiler.service.models.ValidationResponse;
import com.customer.profiler.service.models.ValidationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductValidationService implements IValidationService {

    Logger LOGGER = LoggerFactory.getLogger(ProductValidationService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubscribedProductRespository subscribedProductRespository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductValdiationAPIService productValdiationAPIService;

    @Override
    @CachePut(value = "profiles",key = "#profileId")
    public CustomerProfile validate(Integer profileId) throws ProfilerException {
        try {
                CustomerProfile customerProfile = null;
                Profile profile = getProfileFromDataBase(profileId);
                if(profile.getDeleteDate()!=null || !profile.getValidationStatus().equals(ValidationStatus.CREATED))
                    return createNewCustomerProfile(profile);
                setValidationStatus(profile,ValidationStatus.IN_PROGRESS);
                customerProfile = createNewCustomerProfile(profile);
                CompletableFuture<ValidationResponse>[] completableFutures= startAndWaitForValidation(customerProfile);
                updateValidationStatus(completableFutures,profile);
                return createNewCustomerProfile(profile);
        } catch (Exception exception){
            handleFailedValidation(profileId);
            throw new ProfilerException(exception.getMessage());
        }
    }

    @Override
    //@CachePut(value = "profiles",key = "#profileId")
    public CustomerProfile handleFailedValidation(Integer profileId) throws ProfilerException {
        try {
            Profile profile = getProfileFromDataBase(profileId);
            if(profile.getDeleteDate()!=null || !profile.getValidationStatus().equals(ValidationStatus.IN_PROGRESS))
                return createNewCustomerProfile(profile);
            setValidationStatus(profile,ValidationStatus.FAILED);
            return createNewCustomerProfile(profile);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new ProfilerException(exception.getMessage());
        }
    }

    private boolean isValidated(CompletableFuture<ValidationResponse>[] validationResponses) throws ExecutionException, InterruptedException {
        for(CompletableFuture<ValidationResponse> validationResponse : validationResponses){
            ValidationResponse response = validationResponse.get();
            LOGGER.info(response.getProfileId()+" "+response.getValidationStatus());
            if(response.getValidationStatus().equals(ValidationStatus.REJECTED))
                return false;
        }
        return true;
    }

    private Profile getProfileFromDataBase(Integer profileId){
        Optional<Profile> fetchedProfile = profileRepository.findById(profileId);
        Profile profile = fetchedProfile.get();
        return profile;
    }

    private void setValidationStatus(Profile profile,ValidationStatus validationStatus){
        profile.setValidationStatus(validationStatus);
        this.profileRepository.save(profile);
    }

    private void updateValidationStatus(CompletableFuture<ValidationResponse>[] completableFutures, Profile profile) throws ExecutionException, InterruptedException {
        if (isValidated(completableFutures))
            profile.setValidationStatus(ValidationStatus.APPROVED);
        else
            profile.setValidationStatus(ValidationStatus.REJECTED);
        profileRepository.save(profile);
    }

    private CompletableFuture<ValidationResponse>[] startAndWaitForValidation(CustomerProfile customerProfile) throws InterruptedException, ProfilerException {
        List<SubcribedProduct> products = subscribedProductRespository.findByCustomerId(customerProfile.getCustomerId());
        CompletableFuture<ValidationResponse>[] completableFutures = new CompletableFuture[products.size()];
        int count = 0;
        for(SubcribedProduct product : products) {
            completableFutures[count++] = productValdiationAPIService.validate(customerProfile, product.getProduct().getProductApiLink());
        }
        CompletableFuture.allOf(completableFutures).join();
        return completableFutures;
    }

    private CustomerProfile createNewCustomerProfile(Profile profile) {
        return this.objectMapper.map(profile);
    }
}

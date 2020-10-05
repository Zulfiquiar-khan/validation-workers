package com.customer.profiler.dao.repo;

import com.customer.profiler.dao.models.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends CrudRepository<Profile,Integer> {

    Profile findByCustomerId(Integer customerId);
}

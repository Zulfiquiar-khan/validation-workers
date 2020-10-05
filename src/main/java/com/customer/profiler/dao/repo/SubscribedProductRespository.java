package com.customer.profiler.dao.repo;

import com.customer.profiler.dao.models.SubcribedProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribedProductRespository extends CrudRepository<SubcribedProduct,Integer> {

    List<SubcribedProduct> findByCustomerId(Integer customerId);
}

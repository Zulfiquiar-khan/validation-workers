package com.customer.profiler.dao.models;

import javax.persistence.*;

@SuppressWarnings("ALL")
@Table(name = "subscribed_product")
@Entity
public class SubcribedProduct {

    @Id
    @Column(name = "subscribed_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer subsribedId;

    @Column(name = "customer_id")
    private Integer customerId;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    public Integer getSubsribedId() {
        return subsribedId;
    }

    public void setSubsribedId(Integer subsribedId) {
        this.subsribedId = subsribedId;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

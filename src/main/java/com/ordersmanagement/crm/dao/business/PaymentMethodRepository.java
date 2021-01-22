package com.ordersmanagement.crm.dao.business;

import com.ordersmanagement.crm.models.entities.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    boolean existsByTypeName(String name);
}
